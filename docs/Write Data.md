#Write your Data on the Server

###Write simple Object

Define your Class

    public class Person extends SchemaNode{
        public String name;
    }

Create your Object

    Person person = new Person();
    person.name = "Alex";

Write it with the Client

    StructrConnector.write(person).executeAsync(listener);

###Write multiply objects and relationships with one call
Person lives in a City Example:

    public class City extends SchemaNode{
        public String name;
    }

    public class Person extends SchemaNode{
        public String name;
        public City city;
    }
    
####When you have a city which already exists on the server
In this case you just need to add the object to the city. The client will create a new Person Node and adds a new relation between the city and the person.

    City city = getExistingCity(); // this call is just a placeholder
    
    Person person = new Person();
    person.name = "Alex";
    person.city = city;
    
    StructrConnector.write(person).executeAsync(listener);

####When you have a new city and a new person
In this case you just create two new objects and link them to each other. The client will first generate the city object and then the person with the desired relation.

    City city = new City();
    city.name = "Dortmund";
    
    Person person = new Person();
    person.name = "Alex";
    person.city = city;
    
    StructrConnector.write(person).executeAsync(listener);

###Update your existing Data on the Server
When you want to update a existing object then you do the same as you do when you create a new object. The client will first verify for an existing id and when its available the object will be updated

    City city = getExistingCity(); // this call is just a placeholder
    city.name = "Berlin";
    
    StructrConnector.write(city).executeAsync(listener);