find target/2017-05-26/*.pat | xargs -t -n 1 -I file java -Xmx4096m -Xss512m -jar target/string-matcher-1.0-SNAPSHOT-jar-with-dependencies.jar file target/2017-05-26.fasta
# java -Xmx4096m -Xss512m -jar target/string-matcher-1.0-SNAPSHOT-jar-with-dependencies.jar target/CDS-2017-04-20/A104R_47343.pat target/2013.fasta
