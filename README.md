# YumYumNow Delivery Microservice

## How to Run

The microservice can be run by starting the Spring application. You can then [use Postman](#using-postman)
to perform requests.

## Using Postman

### Setup

In the repository's root, you will find a file with the extension `.postman_collection.json`. It contains template requests that you can use, including customizable collection variables.

To import the template file, either (1) drag the file from your file explorer into your Postman 'Collections' window, or (2) press the 'Import' button in Postman and browse to it.

> Note that your imported collection will **not** auto-update. You must manually reimport changes acquired from the repository.

### Request Requirements

Two things are required to successfully make a request to the microservice:
1) The `X-User-Id` header must be set to a valid `UUID`.
2) The query parameter `role` is one of the following: [`customer`, `courier`, `vendor`, `admin`].

>An example request URL would be `host:port/delivery/?role=courier`.

### Collection Variables

The Postman request collection contains **collection variables** which specify various request values. They are found under _Variables_ when inspecting the collection itself.

Variables include **host** (which defaults to `localhost:8082`), **vendorId** (the default vendor UUID to use), and many more.

