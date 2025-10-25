##
## Backend
##
.PHONY: backend-build
backend-build:
	cd backend && \
	docker build -t interview-backend:latest .

.PHONY: backend-run
backend-run: backend-build
	docker run \
		-it \
		--rm \
		--name interview-backend \
		-p 8080:8080 \
		interview-backend:latest
