# JavaPipBoyServer 
A Fallout 4 PipBoy compatible server implementation.

#### Install & Test  
[![Build Status](https://travis-ci.org/luckydonald/JavaPipBoyServer.svg?branch=master)](https://travis-ci.org/luckydonald/JavaPipBoyServer) [![Coverage Status](https://coveralls.io/repos/github/luckydonald/JavaPipBoyServer/badge.svg?branch=master)](https://coveralls.io/github/luckydonald/JavaPipBoyServer?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/grade/d0fbe9967ed44eadb1997a1e6522ccb0)](https://www.codacy.com/app/t-duebel/JavaPipBoyServer)

```sh
mvn validate    # this installs required .jar files
mvn clean test  # the actual test
```
Now you are ready to go.

#### Working:
- Discovery
- KeepAlive
- Database + structure
- Packaging data for sending
- My first project which uses tests `:D` [![Build Status](https://travis-ci.org/luckydonald/JavaPipBoyServer.svg?branch=master)](https://travis-ci.org/luckydonald/JavaPipBoyServer)


#### Known Bugs
 - not complete
 - this is pre-alpha.
 - Currently Discovery is working, and it stayes connected, but the mobile apps don't display any data (changes). The reason why I yet have to figure out.
 - you might be able to get this working with iOS, but I don't have a new enough device, so I can't say with any certainty.
 - Not compatible with Python 2.7

#### Roadmap
 - Fix Server.

#### FAQ
- Will it emulate the PC or PS4?

    > You can choose either.

- Will it emulate the XBOX?
 
    > XBOX uses smartglass, which works totally differently than the PC server would.
  
- Why is this not Python 2.7 compatible? 
 
    > Because Python 3 and unicode strings are the future!
    > Aaand because this project is in java.
 
