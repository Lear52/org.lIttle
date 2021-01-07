del certificates.jks
del lear.cer
del certificates.p12
del certificate.pem

c:\java\jdk8u265\bin\keytool -genkeypair -dname "CN=lear,OU=Компьютеры,OU=Нижний Новгород,OU=МЦОИ,DC=vip,DC=cbr,DC=ru"  -alias lear -keyalg RSA -keystore certificates.jks -validity 999 -keysize 1024 -keypass 123456 -storepass 123456 -ext san=dns:localhost
C:\Java\jdk8u265\bin\keytool -list -keystore certificates.jks -storepass 123456 > certificates.lst

C:\Java\jdk8u265\bin\keytool -importkeystore -srckeystore certificates.jks -srcstorepass 123456 -srckeypass 123456 -srcalias lear -destalias lear -destkeystore certificates.p12 -deststoretype PKCS12 -deststorepass 123456 -destkeypass 123456

C:\Java\OpenSSL-Win64\bin\openssl pkcs12 -in certificates.p12 -nodes -nocerts -out privatekey.key

C:\Java\jdk8u265\bin\keytool -exportcert -alias lear -keystore certificates.jks -storepass 123456 -file  certificate.cer
C:\Java\jdk8u265\bin\keytool -exportcert -alias lear -keystore certificates.jks -storepass 123456 -rfc -file  certificate.pem
copy cacerts.org cacerts

C:\Java\jdk8u265\bin\keytool -importcert -alias lear -keystore cacerts  -storepass changeit -file  certificate.cer

