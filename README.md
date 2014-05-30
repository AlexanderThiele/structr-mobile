#Structr Mobile Client

Connect your mobile Application simple and secure to Structr - the Open Source Web Application Framework and CMS based on the popular Neo4j Graph Database.

Generate your data schema in the UI and connect your mobile Application immediately and asynchronous to the Server.

### How it works (short overview)
* Generate your schema with structr
* Create your local classes
* Query and use your data

##Step-by-Step
Note: these steps do not show how to use structr or how to create the schema. This example is based on Android.

###Generate your data schema with Structr

First create your custom schema using structr. In this Example the schema is defined as followed:

![Structr Schema Example](.images/example_schema.png?raw=true "Structr Schema Example")

The main entity is Whisky and has a "name" Attribute. it's connected with Location (n->1) and Flavour (n->1). 


###Creating your local Classes
In order to use the entities in your project you need to create the schema classes. Your custom schema class must extend SchemaNode.

In this first example we create the Whisky and Flavour Class.

* The "name" and "description" Attributes are simple String Types. 
* The relation between Whisky and Flavour (n->1) means that Whisky only has 1 Flavour. so just reference a Attribute to Flavour in this Class. 
* The relation between Flavour and Whisky (1->n) is a unknown size of Whiskies. In this case use a ArrayList with a Whisky Class Attribute.

result: 

    public class Whisky extends SchemaNode {

        public String name;
        public Flavour flavour;

    }
    
    public class Flavour extends SchemaNode{

        public String name;
        public String description;
        public ArrayList<Whisky> whiskies;

    }

###Initialize the Client
Initialize the Client with your custom server URL. 

    StructrConnector.connect("structr.org:8082");
    //or with credentials
    StructrConnector.connect("structr.org:8082", "username", "password);

###The first query
The Client provides several methods to interact with the Server. For a quick insight we want to get all whiskies stored in the database.

    StructrConnector.read(Whisky.class).executeAsync(asyncListener);

Since the query is asynchronous we need to add a Listener. The Listener provides 2 methods which are called on Success or Error. 

If the query success -> use the the Data :)

    @Override
    public void onAsyncGetComplete(ArrayList<Whisky> results) {
        for (Whisky whisky : results){
            Log.i("Android Example", "Whisky: " + whisky.name +" Flavour: " + whisky.flavour.name);
        }
    }

For each data the client generates a Whisky- and if available a Flavour-object. You can easily access/change the data and send them back to the server. 

If the relation is n->n or 1->n the objects are stored inside the defined ArrayList (f.e. query a flavour and all whiskies are stored inside the whiskies ArrayList-Attribute).

Thats it! Simple!

###Define your Query
You don't want to query all whiskies everytime. So lets query for a Whisky with the name "Dalmore":

    StructrConnector.read(Whisky.class,"name=Dalmore").executeAsync(asyncListener);

You can add as many parameters as you want (seperated with ',').

###Write your Data
Create your local object and write it to the server.
    
    Whisky whisky = new Whisky();
    whisky.name = "Lagavulin";
    StructrConnector.write(whisky).executeAsync(asyncListener);

The asyncListener is called with the final written object (with server generated id).
    
--------------

#Whats next?
* Improve querys (Get, Post, Put, Delete)
* Object-id Mapping
* Server status ping

##To be implemented
* Security
* Server generated Api-keys for each connected device
* User/Usage statistics
* Mobile oauth (facebook, google, twitter...)
* iOS support   
* Windows Phone support

------------

###Copyright and license
* Copyright 2014 Alexander Thiele
* This product is licensed under GPLv3