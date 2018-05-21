# ftcdev-ctrldoce 

New version for ftcgedoc, a web application developed originally for store invoices from supliers and just included one admin user and the "Supliers" module with options to upload and view three documents associated to a buy operation: PDF, XML and PDF aknowledgment. After that, we include the Clients section, to upload own invoices and use it as a repository, but this little change involve the "User Management" module, which manage users from inside the company and suppliers. 

After this change, all the structure and logic of the web app was very different, but everithing was developed under the same structure, a real problem at this momment, that's why this initiative started, transforming step by step the structure and technology used for build this webapp. We start pointing some best practices under the initial structure.

# First challenge
The first step is convert the project from ant (netBeans) to a maven project. The first challenge is include two old libs:
we use TSMIUtil.jar and TSMIMail.jar, both of them are old external dependencies that may be never be updated, so please install this dependencies on a local repository under this versions:

  mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=<path-to-TSMIUtil.jar> -DgroupId=ftc-devs-utils \ 
                         -DartifactId=<TSMIUtil> -Dversion=<1.0.0> \
                         -Dpackaging=<jar> -DlocalRepositoryPath=</ftc/libs/>


  mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file \
                         -Dfile=<path-to-TSMIMail.jar> -DgroupId=ftc-devs-utils \ 
                         -DartifactId=<TSMIMail> -Dversion=<1.0.0> \
                         -Dpackaging=<jar> -DlocalRepositoryPath=</ftc/libs/>

# Second challenge
Change the statics methods used in many class that represent a model and also has functions. So, first we decouple the model and then transform the in DAO and BO objects. In this way we decouple the model an functionallity.
  
