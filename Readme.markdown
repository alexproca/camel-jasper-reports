#Camel component for jasper reports

This is a camel component that eases report creation with jasper reports framework. The component is inspired by 
camel xslt component.

#Get started

    mvn clean install
  
start a karaf instance and run:  

    feature:repo-add mvn:org.apache.camel/camel-jasper-reports/2.13.0-SNAPSHOT/xml/features
    feature:repo-refresh
    feature:install camel-jasper-reports-test

This will create a pdf report in `/tmp/out-pdf` every 10 seconds. If you would like to use in a real 
karaf use the `camel-jasper-reports` feature. Be careful tough with production environments, **this is an alpha version**.  