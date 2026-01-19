Admin app (minimal Angular skeleton)

Setup
1. cd admin-app
2. npm install
3. npm run start:proxy

Notes
- The proxy forwards /api and /oauth2 to http://localhost:8082 so backend auth cookies will be same-origin in dev.
- This is a minimal scaffold. If you prefer a full Angular CLI project, run `ng new admin-app` and copy the `src/app/users` files over, then add the proxy.conf.json.
