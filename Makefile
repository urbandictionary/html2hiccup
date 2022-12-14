default:
	cat Makefile

.PHONY: test
test:
	lein bat-test auto

format:
	find . -name \*.clj -or -name \*.edn -or -name \*.cljc -or -name \*.cljs | xargs zprint -w

watch:
	lein run -m shadow.cljs.devtools.cli watch app
