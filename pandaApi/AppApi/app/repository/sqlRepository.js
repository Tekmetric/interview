const sqlite3 = require('sqlite3').verbose();
const dbFile = './database/db.sqlite';

module.exports = {

    all(sqlCommand) {
        return new Promise((res, err) => {
            try {
                var dbRecords = [];
                var db = new sqlite3.Database(dbFile);

                return db.all(sqlCommand, [], (dbErr, rows) => {
                    if (dbErr) {
                        db.close();
                        return err(dbErr);
                    }

                    rows.forEach((row) => {
                        dbRecords.push(row);
                    });
                    db.close();
                    return res(dbRecords);
                });
            } catch (ex) {
                err(ex);
            }
        });
    },

    filter(sqlCommand, params) {
        return new Promise((res, err) => {
            try {
                var dbRecords = [];
                var db = new sqlite3.Database(dbFile);

                return db.all(sqlCommand, params, (dbErr, rows) => {
                    if (dbErr) {
                        db.close();
                        return err(dbErr);
                    }

                    rows.forEach((row) => {
                        dbRecords.push(row);
                    });
                    db.close();
                    return res(dbRecords);
                });
            } catch (ex) {
                err(ex);
            }
        });
    },

    run(sqlCommand, params) {
        return new Promise((res, err) => {
            try {
                var db = new sqlite3.Database(dbFile);

                return db.run(sqlCommand, params, (dbErr) => {
                    if (dbErr) {
                        db.close();
                        return err(dbErr);
                    }
                    db.close();
                    res(this.rowId);
                });
            } catch (ex) {
                err(ex);
            }
        });
    }

}