package de.disk0.ldap.api.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.odm.annotations.Entry;

import de.disk0.dbutil.api.entities.BaseGuidEntity;

@Table(name="entry")
@Entry(objectClasses = {"organization", "organizationalUnit", "groupOfUniqueNames", "inetPerson" })
public class LdapEntry extends BaseGuidEntity {
	
	private static Log log = LogFactory.getLog(LdapEntry.class);	

	@Column(name="type")
	private EntryType type;
	
	// ruben.malchow == uid
	@Column(name="name")
	private String name;
	
	// "ruben malchow" == cn
	@Column(name="display_name")
	private String displayname;
	
	// "ruben" == givenName
	@Column(name="given_name")
	private String givenname;
	
	// "malchow" == surname
	@Column(name="family_name")
	private String familyname;
	
	// "ruben.malchow@example.com" == mail
	@Column(name="email")
	private String email;
	
	@Column(name="path")
	public String path;
	
	@Column(name="parent_id")
	private String parentId;
	
	@Column(name="dn")
	public String dn;

	@Column(name="description")
	private String description;

	@Column(name="updated")
	private Date updated;

	@Column(name="ignored")
	private boolean ignored = false;

	@Column(name="enabled")
	private boolean enabled = false;

	@Column(name="system")
	private boolean system = true;

	public EntryType getType() {
		return type;
	}

	public void setType(EntryType type) {
		this.type = type;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public List<String> getHierarchy() throws InvalidNameException {
		LdapName n = new LdapName(getDn());
		List<String> out = new ArrayList<String>();
		
		for(Rdn r : n.getRdns()) {
			out.add(r.getValue()+"");
		}
		
		return out;
	}
	
	public List<String[]> getAncestors() throws InvalidNameException {
		List<String[]> out = new ArrayList<>();
		try {

			List<String> ids = new ArrayList<>();
			String[] x = getPath().split("\\.");
			for(int i=1;i<x.length;i++) {
				ids.add(0,x[i]);
			}
			
			List<String> rdns = new ArrayList<>();
			for(Rdn r : new LdapName(getDn()).getRdns()) {
				rdns.add(0,r.getValue().toString());
			}
			
			while(true) {
				if(ids.size()==0) break;
				if(rdns.size()==0) break;
				if(ids.size()==1) {
					out.add(0,new String[] {
						StringUtils.join(rdns, "."),
						ids.remove(0)
					});
					break;
				} 
				out.add(0,new String[] {
					rdns.remove(0),
					ids.remove(0)
				});
			}
			out.add(0, new String[] { "ROOT", "00000000-0000-0000-0000-000000000000" });
		} catch (Throwable e) {
			log.warn("error building ancestors",e);
		}
		return out;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getGivenname() {
		return givenname;
	}

	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}

	public String getFamilyname() {
		return familyname;
	}

	public void setFamilyname(String familyname) {
		this.familyname = familyname;
	}
	
	
	@Override
	public String toString() {
		return " { id="+getId()+", displayname="+getDescription()+", dn="+getDn()+"}";
	}
	
	
}
