name := "vdnastool-server"
 
version := "1.0" 
      
lazy val `nastool` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

val playPac4jVersion = "6.1.0"
val pac4jVersion = "3.4.0"
val playVersion = "2.6.20"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  guice,
  ehcache,
  "com.h2database"        % "h2"                                  % "1.4.197",
  "org.postgresql"        % "postgresql"                          % "9.4-1200+",
  "org.skinny-framework"  %% "skinny-orm"                         % "3.0.0",
  "org.scalikejdbc"       %% "scalikejdbc"                        % "3.3.1",
  "org.scalikejdbc"       %% "scalikejdbc-config"                 % "3.3.1",
  "org.scalikejdbc"       %% "scalikejdbc-play-initializer"       % "2.6.0-scalikejdbc-3.3",
  "org.scalikejdbc"       %% "scalikejdbc-play-dbapi-adapter"     % "2.6.0-scalikejdbc-3.3",
  "org.pac4j"             %% "play-pac4j"                         % playPac4jVersion,
  "org.pac4j"             % "pac4j-oidc"                          % pac4jVersion exclude("commons-io" , "commons-io"),
  "com.typesafe.play"     % "play-cache_2.12"                     % playVersion,
  "commons-io"            % "commons-io"                          % "2.4",
  "org.apache.shiro"      % "shiro-core"                          % "1.4.0"
)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

      