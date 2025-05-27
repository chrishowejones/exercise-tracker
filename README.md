# exercise-tracker

This project is an Exercise tracker example application written with ClojureScript using Reagent and Figwheel-main as
the build tool.

## Overview

This is an example application derived from the online book [Learn ClojureScript](https://www.learn-clojurescript.com/)

## Development

To get an interactive development environment run:

    clojure -M:fig:build

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    rm -rf target/public

To create a production build run:

	rm -rf target/public
	clojure -M:fig:min


## License

Copyright © 2025 Chris Howe-Jones

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
