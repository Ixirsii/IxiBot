module tech.ixirsii.ixibot {
    requires static lombok;

    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.module.paramnames;
    requires org.slf4j;
    requires reactor.core;
    requires com.google.guice;
    requires jakarta.inject;
    requires com.google.common;
}
