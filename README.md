# YumYumNow Delivery Microservice

## How to Run

The microservice can be run by starting the Spring application. You can then [use Postman](#using-postman)
to perform requests.

## Using Postman

### Setup

In the repository's root, you will find a file with the extension `.postman_collection.json`. It contains template requests that you can use, including customizable collection variables.

To import the template file, either (1) drag the file from your file explorer into your Postman 'Collections' window, or (2) press the 'Import' button in Postman and browse to it.

### Request Requirements

Two things are required to successfully make a request to the microservice:
1) The `X-User-Id` header must be set to a valid `UUID`.
2) The query parameter `role` is one of the following: [`customer`, `courier`, `vendor`, `admin`].

>An example request URL would be `host:port/delivery/?role=courier`.

### Collection Variables

The Postman request collection contains **collection variables** which specify various request values. They are found under _Variables_ when inspecting the collection itself.

Variables include **host** (which defaults to `localhost:8082`), **vendorId** (the default vendor UUID to use), and many more.

# Lab Template

This template contains two microservices:
- authentication-microservice
- example-microservice

The `authentication-microservice` is responsible for registering new users and authenticating current ones. After successful authentication, this microservice will provide a JWT token which can be used to bypass the security on the `example-microservice`. This token contains the *NetID* of the user that authenticated. If your scenario includes different roles, these will have to be added to the authentication-microservice and to the JWT token. To do this, you will have to:
- Add a concept of roles to the `AppUser`
- Add the roles to the `UserDetails` in `JwtUserDetailsService`
- Add the roles as claims to the JWT token in `JwtTokenGenerator`

The `example-microservice` is just an example and needs to be modified to suit the domain you are modeling based on your scenario.

The `domain` and `application` packages contain the code for the domain layer and application layer. The code for the framework layer is the root package as *Spring* has some limitations on were certain files are located in terms of autowiring.

## Running the microservices

You can run the two microservices individually by starting the Spring applications. Then, you can use *Postman* to perform the different requests:

Register:
![image](instructions/register.png)

Authenticate:
![image](instructions/authenticate.png)

Hello:
![image](instructions/hello.png)
