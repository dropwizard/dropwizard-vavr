Dropwizard Vavr Bundle
======================

[![Build](https://github.com/dropwizard/dropwizard-vavr/workflows/Java%20CI/badge.svg)](https://github.com/dropwizard/dropwizard-vavr/actions?query=workflow%3A%22Java+CI%22)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dropwizard_dropwizard-vavr&metric=alert_status)](https://sonarcloud.io/dashboard?id=dropwizard_dropwizard-vavr)
[![Maven Central](https://img.shields.io/maven-central/v/io.dropwizard.modules/dropwizard-vavr.svg)](http://mvnrepository.com/artifact/io.dropwizard.modules/dropwizard-vavr)

A [Dropwizard] bundle for integrating [Vavr] in [Dropwizard] applications.

The package provides integration of [Vavr] classes into Jackson (via [vavr-jackson]), Jersey (via custom classes), and Jdbi 3 (via [jdbi3-vavr]).


[Vavr]: http://www.vavr.io/
[Dropwizard]: http://dropwizard.io/
[vavr-jackson]: https://github.com/vavr-io/vavr-jackson
[jdbi3-vavr]: https://github.com/jdbi/jdbi

Usage
-----

Just add `VavrBundle` to your Dropwizard application as a [Bundle](https://www.dropwizard.io/en/release-2.0.x/manual/core.html#bundles).

    public class DemoApplication extends Application<DemoConfiguration> {
        // [...]
        @Override
        public void initialize(Bootstrap<DemoConfiguration> bootstrap) {
            bootstrap.addBundle(new VavrBundle());
            // [...]
        }
    }


Maven Artifacts
---------------

This project is available on Maven Central. To add it to your project simply add the following dependencies to your
`pom.xml`:

    <dependency>
      <groupId>io.dropwizard.modules</groupId>
      <artifactId>dropwizard-vavr</artifactId>
      <version>2.0.0-1</version>
    </dependency>


Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/dropwizard/dropwizard-vavr/issues).


License
-------

Copyright (c) 2017-2020 Jochen Schalanda, Dropwizard Team

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
