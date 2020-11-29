waitForMysql() {
	start_ts=$(date +%s)
	while :
	do 
		nc -z -w 5 ${MYSQL_HOST} ${MYSQL_PORT}
		result=$?
		end_ts=$(date +%s)
		echo "waiting for mysql://${MYSQL_HOST}:${MYSQL_PORT} ... ($((end_ts - start_ts)) / 180s)"
		sleep 5
		if [ ${result} -eq 0 ]; then
			echo "mysql available after $((end_ts - start_ts)) seconds"
			return;			
		fi
		if [  $((end_ts - start_ts)) -ge 180 ]; then 
			echo "mysql NOT available after $((end_ts - start_ts)) seconds ... exiting"
			exit 1
		fi
	done
}

if [ -z "${JAVA_OPTS}" ]; then 
	JAVA_OPTS=${JAVA_OPTS:--Djava.security.egd=file:/dev/urandom}
fi



for i in MYSQL_HOST MYSQL_PORT LDAP_ADMIN_PW; do
	if [ ! -v ${i} ]; then
		echo "${i} not set ... exiting"
		exit 1
	fi
done

waitForMysql
while : 
do
	java -jar ldap-app.jar -o -Xmx512m
	echo "ldap app crashed, restarting ... "
	sleep 10
done