import com.lightbend.sbt.javaagent.JavaAgent.JavaAgentKeys.javaAgents
import sbt.Keys.libraryDependencies
import sbt._


object Version {
  val PekkoVersion = "1.1.3"
  val PekkoMgmtVersion = "1.1.0"
  val postgresVersion = "42.7.5"
  val PekkoProjectionVersion = "1.1.0"
  val r2dbcPostgresVersion = "1.0.7.RELEASE"
  val PekkoPersistenceR2dbcVersion = "1.1.0-M1"
  val PekkoProjectionR2dbcVersion = "1.1.0-M1"
  val logbackVersion = "1.2.3"
  val kamonVersion = "2.7.5"
  val kanelaAgentVersion = "1.0.18"

  val PekkoHttpVersion = "1.1.0"
}

object Dependencies {
  val dependencies = Seq(
    "org.apache.pekko" %% "pekko-connectors-kafka" % "1.1.0",

    "org.apache.pekko" %% "pekko-protobuf-v3" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-serialization-jackson" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-stream" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-persistence" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-cluster" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-stream-testkit" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-actor-testkit-typed" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-cluster-sharding" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-cluster-sharding-typed" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-cluster-tools" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-cluster-typed" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-coordination" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-discovery" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-distributed-data" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-serialization-jackson" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-testkit" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-actor-typed" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-persistence-query" % Version.PekkoVersion,
    "org.apache.pekko" %% "pekko-persistence-typed" % Version.PekkoVersion,

    "org.apache.pekko" %% "pekko-http-spray-json" % Version.PekkoHttpVersion,

    "org.apache.pekko" %% "pekko-discovery-kubernetes-api" % Version.PekkoMgmtVersion,
    "org.apache.pekko" %% "pekko-management-cluster-http" % Version.PekkoMgmtVersion,
    "org.apache.pekko" %% "pekko-management-cluster-bootstrap" % Version.PekkoMgmtVersion,
    "org.postgresql" % "postgresql" % Version.postgresVersion,


    "org.apache.pekko" %% "pekko-persistence-r2dbc" % Version.PekkoPersistenceR2dbcVersion,
    "org.apache.pekko" %% "pekko-projection-r2dbc" % Version.PekkoProjectionR2dbcVersion,
    "org.apache.pekko" %% "pekko-projection-core" % Version.PekkoProjectionVersion,
    "org.apache.pekko" %% "pekko-projection-eventsourced" % Version.PekkoProjectionVersion,

    "org.postgresql" % "r2dbc-postgresql" % Version.r2dbcPostgresVersion,

    "ch.qos.logback"    % "logback-classic"       % Version.logbackVersion,
    "org.apache.commons" % "commons-lang3" % "3.9",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.11.0",
    "commons-io" % "commons-io" % "2.11.0",
    "org.apache.commons" % "commons-collections4" % "4.4",

    "org.hibernate.validator" % "hibernate-validator" % "7.0.1.Final",
    "org.hibernate.validator" % "hibernate-validator-annotation-processor" % "7.0.1.Final",
    "javax.el" % "javax.el-api" % "3.0.0",
    "org.glassfish" % "jakarta.el" % "4.0.1",
    "org.glassfish.web" % "javax.el" % "2.2.6", //Mind that the import is needed.
    "jakarta.validation" % "jakarta.validation-api" % "3.0.1",
    "com.vladsch.flexmark" % "flexmark-all" % "0.50.42",
    "com.mashape.unirest" % "unirest-java" % "1.4.9"

  )

  val kamonSettings = Seq(
    javaAgents += "io.kamon" % "kanela-agent" % Version.kanelaAgentVersion,
    /*javaOptions in Universal += "-DKamon.auto-start=true",*/
    libraryDependencies ++= Seq(
      "io.kamon" %% "kamon-bundle" % Version.kamonVersion,
      "io.kamon" %% "kamon-prometheus" % Version.kamonVersion
    ))

}