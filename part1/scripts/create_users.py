serverConfig()

print 'lookup DefaultAuthenticator' 

password = 'welcome1'

atnr=cmo.getSecurityConfiguration().getDefaultRealm().lookupAuthenticationProvider('DefaultAuthenticator')

group = 'approvers'
print 'create group '+group
atnr.createGroup(group,group)

# creting users for request
users = ['appadmin', 
	'appmkt', 
	'apppurch', 
	'apphr', 
	'appship', 
	'appit', 
	'apppublic',
	'appsales',
	'appexec',
	'appfinance',
	'appaccount']

for user in users:        
  print 'create user: ',user
  atnr.createUser(user,password,user)
  atnr.addMemberToGroup(group,user)
