# SmMailService
SmMailService is a REST based web service which exposes an API to send emails to multiple recipients (to, cc and bcc) along with multiple attachments using SendGrid or MailGun e-mailing services. Priority in which the e-mailing services should be used can be configured using application configurations. Please refer the service configurations to know further.

# How to start the service ?

* Clone this repo and traverse to {root}/release/.

* Set the API end points, api keys or tokens required to consume MailGun or SendGrid mail service.
    * `export SENDGRID_KEY={AUTH_TOKEN}`
    * `export MAILGUN_KEY={API-KEY}`
    * `export SENDGRID_ENDPOINT={SENDGRID-API-ENDPOINT}`
    * `export MAILGUN_ENDPOINT={MAILGUN-API-ENDPOINT}`

* Run `java -jar challenge-0.0.1-SNAPSHOT.jar`

__NOTE :__

SENDGRID_KEY should be the {AUTH_TOKEN} alone. **Should not be in the format of `Bearer {AUTH_TOKEN}`.**

MAILGUN_KEY should be the entire user:password. **e.g. api:xxxxxxxxxx**

SENDGRID_ENDPOINT should be the complete URL of SendGrid API end-point (https://api.sendgrid.com/v3/mail/send).

MAILGUN_ENDPOINT should be the complete URL of MailGun API end-point (https://api.mailgun.net/v3/{MAILGUN_DOMAIN}/messages).

# REST API - Usage

## Example
```bash
curl -X POST \
  http://localhost:8080/emails \
  -F 'email_params={
     "from": {
         "email" : "hariharan.rajend@gmail.com"
     },
     "to" : [
        {
            "name" : "Hari Since",
            "email" : "hari.since1987@gmail.com"
        }
     ],
     "bcc" : [
        {
            "name" : "Hari Elearner",
            "email" : "hari.elearner@gmail.com"
        }
     ],
     "subject" : "sample",
     "message" : "sample"
   }' \
 -F attachments=@/mnt/hgfs/Data/sm-test-files/sample.pdf \
 -F attachments=@/mnt/hgfs/Data/sm-test-files/sample.jpg \
 -F attachments=@/mnt/hgfs/Data/sm-test-files/sm-test-20MB.txt
```

## Details

**HTTP METHOD** : POST

**END POINT** : https://{host}:8080/emails

**FORM PARAMETER** : email_params (#Required #MustOccurOnce)

**FORM PARAMETER** : attachments (#Optional #MoreThanOnce)

**ERROR CODES:**

202 - Success - Mail request successfully queued

400 - Bad Request - Missing required fields, Invalid email formats, Duplicate recipients, Mail size exceeded, Zero sized attachments

500 - Internal server errors

## What is email_params
email_params is a JSON string with which the consumer can specify the details of the e-mail to be sent. Schema of the email_params JSON string is stated below,

```json
from - Email object containg e-mail address of the sender - #Required
         name - Name of the sender - #Optional
         email - E-mail address of the sender - #Required
         

to - Array of e-mail objects of TO recipients - #Required #NonEmpty
         ArrayElement
                  name - Name of the receiver - #Optional
                  email - E-mail address of the receiver - #Required
                  
cc - Array of e-mail objects of CC recipients - #Optional #CanBeEmpty
         ArrayElement
                  name - Name of the receiver - #Optional
                  email - E-mail address of the receiver - #Required
                  
bcc - Array of e-mail objects of BCC recipients - #Optional #CanBeEmpty
         ArrayElement
                  name - Name of the receiver - #Optional
                  email - E-mail address of the receiver - #Required
                  
subject - Subject of the e-mail - #Required #NonBlank

message - Body of the e-mail - #Required #NonBlank
```
# What is attachments ?

Consumer can attach multiple files using the form parameter key "attachments".

# How to test ?

* If the services is started successfully, you should see the following logs in your console,
```bash
2018-05-27 00:17:26.433  INFO 19294 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2018-05-27 00:17:26.437  INFO 19294 --- [           main] c.siteminder.challenge.SmMailServiceApp  : Started SmMailServiceApp in 2.456 seconds (JVM running for 2.852)
```

* Service should be up and listening on the default port 8080. The port is configurable in application properties. Please refer to the service configuration section to know more further.

* Hit the API as shown in API usage section.

# How to build from source ?

## Pre-requisites
Java - Open JDK 1.8.0_171

Apache Maven 3.3.9

## Steps

* Clone this repo and traverse to the root of your local repo.
* Run `mvn package`
* Target binary will be generated in {root}/target/challenge-0.0.1-SNAPSHOT.jar

# Service Configurations

## Steps
* Stop the service is if it's already running i.e. Press Ctrl+C in the console from which it is launched.

* Open the application.properties (changes based on where the binaries are deployed). If you just folowed the things in this doc, it should be in {root}/release/config directory.

* Configure `server.port` property to set the port in which the server should listen.

* Configure `smchallenge.smMailServiceConfig` to set the priority order of the mailing services. Please refer to the inline comments in properties file to know more.

## Limitations

* Maximum number of email recipients allowed is 1000.

* Maximum size of email (including subject, body and all attachment sizes) should not exceed 20 MB.

* Only plain-text content is allowed in email body.

* Zero sized attachments are not allowed.

## TBD

* Add unit test cases.

* Dockerize the service.

* Cover more features of MailGun or SendGrid.

* Address the in-line TODO's in source code.

* Add support for swagger docs and JavaDoc comments.

* Internationalization of error messages returned from the service (if required).
