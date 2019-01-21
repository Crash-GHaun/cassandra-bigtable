# Migrating from Apache Cassandra to Google Cloud Bigtable

This repository provides support for whitepaper on how to migrate your database from Apache Cassandra (following as “Cassandra”) to Google Cloud Bigtable (following as “Cloud Bigtable”. It describes a migration process that not only moves your database to Cloud Bigtable but also lets you take full advantage of Cloud Bigtable unique capabilities, as well as best practices for ensuring a smooth transition with minimal service impact.

However, due to the nature of this whitepaper and very large variety of possible applications using Apache Cassandra, this document doesn’t cover all possible use-cases and it cannot be used as step-by-step migration guide.

**Included Tools**

The collection of tools to support migration from Apache Cassandra to Google Cloud Bigtable:

 - Apache Beam pipeline running on Google Cloud Dataflow for bulk loading of data
 - Demo Application

