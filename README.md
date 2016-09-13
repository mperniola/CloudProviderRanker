# Cloud Provider Ranker

CloudProviderRanker is a standalone REST WEB Service which ranks cloud providers basing on rules implemented with the Drools framework http://drools.org/

The Indigo Orchestrator interacts with it in order to obtain a ranking of two or more cloud providers basing on particular rules.

The aim of this component is to fully decouple the ranking logic from the Orchestrator's busigness logic.

The Cloud Provider Ranker can be installed on any machine which is reachable from the Orchestrator via TCP connection. It can be set up to listen on plain or on SSL socket, using HTTP as transport.