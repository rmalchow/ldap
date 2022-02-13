cd `dirname $0`
git diff --exit-code 2>&1 > /dev/null
if [ "$?" != "0" ]; then
	echo "there are unstaged changes"
	exit -1
fi

git diff --cached --exit-code  2>&1 > /dev/null
if [ "$?" != "0" ]; then
	echo "there are uncommitted changes"
	exit -2
fi

version=`jq -r '[ (.version | split(".")[0]) ,(.version | split(".")[1]) , ( .version | split(".")[2]  | tonumber + 1 | tostring) ] | join(".")' package.json`
echo "version is ${version}"

package=`jq --arg version ${version} '.version = $version' package.json`
echo ${package} |jq -rM . > package.json

git tag "${version}"
git add .
git commit -m "[skip ci] release version ${version}"
git push

