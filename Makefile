default:
	cat Makefile

.PHONY: test
test:
	lein bat-test auto

format:
	find . -name \*.clj -or -name \*.edn -or -name \*.cljc | xargs zprint -w

shadow:
	 lein run -m shadow.cljs.devtools.cli compile app