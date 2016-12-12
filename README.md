This is your Who''s Looking Atlassian Connect Add-on Play application written in Scala
======================================================================================

Developer Notes
===============

Download Redis:

    wget http://download.redis.io/releases/redis-2.8.6.tar.gz
    tar xzf redis-2.8.6.tar.gz
    cd redis-2.8.6
    make

Start Redis:

    src/redis-server

Start Redis client:

    src/redis-cli

Command to clean the Redis database from Redis client:

    > flushdb

Start JIRA:

* Install Atlassian Plugin SDK 5.0 or above (https://developer.atlassian.com/display/DOCS/Set+up+the+Atlassian+Plugin+SDK+and+Build+a+Project)
* Start JIRA as per instruction from page https://developer.atlassian.com/static/connect/docs/developing/developing-locally.html.

Start whoslooking-connect-scala:

    sbt -Dconfig.file=conf/development-h2.conf run

Heroku Deployment Notes
=======================

Create file `system.properties` with the following content:

    java.runtime.version=1.7
    
Create file `Procfile` witht the following content:

    web: target/universal/stage/bin/whoslooking-connect-scala -Ddb.default.driver=org.postgresql.Driver -Ddb.default.slick.driver=scala.slick.driver.PostgresDriver -Dredis.uri=${REDISCLOUD_URL}
    
Set configuration variable `REDISCLOUD_URL` as following, replacing `user`, `password`, `hostname` and `port` with those provided by Redis Cloud:

    heroku config:set REDISCLOUD_URL=redis://user:password@localhost:6379
