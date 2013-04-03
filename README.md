Hacktors
========

This is a *simple* demo that should exemplify how something similar to a
multiplayer version of Hack could be programmed with JActors and clustered on
Apache Felix.

It is meant to demonstrate different technologies, and not to create the deepest gameplay.
The code should stay as simple as possible, os it keeps an "educational value".

Phase 0 (the current phase):

* Create a simple working prototype, using the usual Java coding model.

Phase 1:

* Convert the code to use JActors, support persistence,  and be multi-threaded.

Phase 2:

* Convert the code, so that it can work in Apache Felix, an OSGi container.

Phase 3:

* Using the appropriate JActor APIs, make the code distributed, and add multi-player support.

Phase 4:

* Convert the code, to allow dynamic extension of the core through thrid-party, uncoordinated, extentions.

OSGi deployment :
* OSGi bundles were tested on [apache-karaf](http://karaf.apache.org/) (2.3.1) 
* Download and extract karaf distributable.
* Edit <karaf_home>/etc/org.ops4j.pax.url.mvn.cfg and add following two maven repository in the comma separated list of repositories :
  https://raw.github.com/skunkiferous/Maven/master 
	http://repository.springsource.com/maven/bundles/release
* Save hactors_karaf_deployer.xml (from https://github.com/skunkiferous/Hacktors) in some directory.
* Open a command prompt and change the current directory to Karaf-home. and Start Karaf using the following command 
	bin/karaf.bat
* On kafar prompt use the following commands to deploy the bundles:
	features:addUrl file:<path_to_hactors_karaf_deployer.xml>
	features:install blockwithme
