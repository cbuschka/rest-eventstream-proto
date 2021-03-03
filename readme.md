# Prototype of Eventstream via REST style API

## What is it?
* Event producer with API to fetch events (starting with event last seen)
* Event consumer polling for events (with exponential, limited back off)
* A simple object store to store the last event seen
* TODO: coordinating multiple consumers, so there is only one actively polling for events

## Prerequisites

* java 11
* maven

## License

[MIT](./license.txt)
