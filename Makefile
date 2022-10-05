default:
	cat Makefile

.PHONY: test
test:
	lein bat-test auto

format:
	find . -name \*.clj -or -name \*.edn | xargs zprint -w