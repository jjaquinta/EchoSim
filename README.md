EchoSim
=======

This is a simulator for the Amazon Echo. It makes the best attempt to replicate what your Echo, and Amazon Alexa services do. It's not produced by Amazon, and isn't guaranteed to be entirely accurate. It isn't a substitute for testing with an actual Echo. However, it is good for initial code proofing, replication of errors, and regression testing.

App Tab
-------
The first tab is where you can instruct EchoSim to connect to your skill.

The “Endpoint” field is the URL to where your Alexa service is served up. EchoSim doesn't care what certificate you use,
or if you even use https. That makes it handy for testing. This can also be a fully qualified name to a class, followed by :: and a method name. EchoSim will try to instantiate that class, and invoke that method to access your service. This is handy for testing Lambda functions from within the development environment.

The “Application ID” field lets you set the application ID, if your skill cares.

Similarly, the “User ID” field lets you pick an ID for your user, if your skill cares.

You can import your intents and utterances either via a file or a URL. Once read in, the data is displayed in the panels. You can look through it to make sure the data you expect is there.

For convienence, the drop down at the top will let you save the current selection of settings as a “preset”.
You can also copy and paste the settings to and from the clipboard in JSON format.

Tester Tab
----------

This lets you interact with your skill.

Just type in the text of what you would want to say into the input box at the bottom of the screen and press enter. The response from your skill appears in a transcript in the main portion of the screen.
You can control what parts of the response are shown by the check boxes at the top of the screen.
As you type in text into the input box, EchoSim displays below it how it will convert that text into an intent and slots. It's not too smart about it, so it is best for testing “known good” phrases.
The “play” button sends a launchRequest intent to your skill, and the “stop” button sends a sessionEndedRequest to your skill. “Clear” removes the data displayed in the transcript.
“Save” and “Load” allow you to record what is displayed in the transcript into an external JSON file.

Script Tab
----------

This is where you can take your interactions from the Test tab and put them together into a script. “Insert History” inserts the current transcript into the script. The controls at the bottom let you move lines of the script around the place and change the match mode between "must match", "musn't match", "don't care", "regex match" and "regex no match".
“Save” and “Load” let you store and retrieve scripts in, guess what, JSON format.

Suite Tab
----------

This lets you group together scripts into a single test suite. Each script will be run in turn and the results accumulated.
"Save" and "Load" do what you expect, but the scripts are noted via their relative file name. That way if you store your scripts in the same directory as the suite, you can move them around and everything will still load.

Testing Lambda
==============

The simplest way to use EchoSim to test a Lambda function is to create a URL endpoint for your Lambda function using Amazon's Gateway. You can find instructions [here](http://docs.aws.amazon.com/apigateway/latest/developerguide/getting-started.html). Once you have a URL endpoint, you can just enter into onto the App Tab as normal.

If your Lambda function is in Java, you can use EchoSim to test it directly from the development environment. Pull down
the EchoSim project from GitHub and create a launch configuration for it. Add to the classpath in the launch configuration
your Lambda function project, all the jars your project uses, and the two Lambda function jar files in the AWS plugin
for Eclipse. You can now launch EchoSim, and give it a package.name.ClassName:method endpoint, and it will invoke your
Lambda function directly.

This is *much* faster than packaging and posting your Lambda function. However, when it calls your metohd, the Context
parameter will be null. You need to make sure you code can handle that condition.

The Book
========

There is an e-book [How To Program -- Amazon Echo](http://www.amazon.com/How-Program-Amazon-Echo-Development-ebook/dp/B011J6AP26)
which contains a whole section on testing Alexa skills, with a lot detail about using EchoSim to do so. It's only $.99 and,
if these instructions aren't enough, it might be worth investing in.

