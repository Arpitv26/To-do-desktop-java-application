module org.example.todo {
    requires javafx.controls;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    opens org.example to com.fasterxml.jackson.databind;
    exports org.example;
}
