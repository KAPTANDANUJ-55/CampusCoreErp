# CampusCore notifications

From `CampusCoreErp/notification-service`, start the service with Java 25.

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux, run `sh ./mvnw spring-boot:run`. The service listens on `127.0.0.1:8084` and stores notifications and read receipts in `data/notifications.mv.db`. To use a MySQL database, set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` for a database created for this service.

Set `JWT_ISSUER` to match the gateway and auth service if they use an issuer other than `campuscore`. The service uses the gateway's public key by default when started from this directory. Set `JWT_PUBLIC_KEY` to a `file:` resource pointing to the public PEM when running from another directory or using a different key.

Restart the API gateway with the updated code. The gateway adds the notifications route automatically and uses `http://127.0.0.1:8084` as its upstream. Set `NOTIFICATIONS_SERVICE_URL` on the gateway to change that address. Start the React app, sign in, and open **Notifications** in the workspace navigation.

| Request | Behavior |
| --- | --- |
| `GET /api/v1/notifications` | Return the inbox, unread count, and pagination cursor. Optional query parameters are `unread=true` and `before=<nextBefore>`. |
| `POST /api/v1/notifications` | Publish with `requestId` (UUID), `title`, `message`, `category` (`ANNOUNCEMENT`, `ACADEMIC`, or `REMINDER`), and `audience` (`EVERYONE`, `STUDENTS`, or `STAFF`). Retry the same request with the same UUID. |
| `POST /api/v1/notifications/{id}/read` | Mark a visible notification as read for the signed-in account. |
| `POST /api/v1/notifications/read-all` | Mark visible notifications through the supplied `throughId` as read. Use the inbox's `latestId` to leave newer notifications unread. |

Principals can publish campus-wide. Heads of department can publish within the department in their signed token. Students and staff can read notifications addressed to their role or to everyone, and any sender can see their own published notifications in their inbox.
