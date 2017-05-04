/* Devices SQL */
DROP TABLE IF EXISTS devices;
CREATE TABLE IF NOT EXISTS devices (address text NOT NULL, createdAt text NOT NULL, updatedAt text NOT NULL, wireless_technology text NOT NULL, battery_level integer(10) NOT NULL, connection_type integer(1) NOT NULL, status integer(1) NOT NULL);
/* Applications SQL */
DROP TABLE IF EXISTS applications;
CREATE TABLE IF NOT EXISTS applications ("identity" text NOT NULL, createdAt text NOT NULL, updatedAt text NOT NULL, ip text NOT NULL, pid integer(10) NOT NULL, status integer(10) NOT NULL);
/* Unsent Data SQL */
DROP TABLE IF EXISTS sensors_data;
CREATE TABLE IF NOT EXISTS sensors_data (sensorAddress text NOT NULL, createdAt text NOT NULL, source text NOT NULL, data blob NOT NULL, status integer(10) NOT NULL);
/* Datatypes SQL */
DROP TABLE IF EXISTS datatypes;
CREATE TABLE IF NOT EXISTS datatypes (id INTEGER NOT NULL PRIMARY KEY, "identity" text NOT NULL, createdAt text NOT NULL, updatedAt text NOT NULL);
/* Routes SQL */
DROP TABLE IF EXISTS routes;
CREATE TABLE IF NOT EXISTS routes (source text NOT NULL, destination text NOT NULL, createdAt text NOT NULL, updatedAt text NOT NULL, qos integer(10) NOT NULL ,status integer(10) NOT NULL);
/* Configurations - Information about Gateway SQL */
DROP TABLE IF EXISTS gwinfo;
CREATE TABLE IF NOT EXISTS gwinfo (configId text NOT NULL, configValue text NOT NULL, createdAt text NOT NULL, updatedAt text NOT NULL);
/* Unity Maps */
DROP TABLE IF EXISTS unity;
CREATE TABLE IF NOT EXISTS unity (address text NOT NULL, sensorId integer(10) NOT NULL);
INSERT INTO unity (address,sensorId) VALUES('B0:B4:48:ED:99:01',0);
/* Configurations */
INSERT INTO gwinfo (configId,configValue,createdAt,updatedAt) VALUES('ble_mode','CONNECTABLE',datetime(),datetime());
INSERT INTO gwinfo (configId,configValue,createdAt,updatedAt) VALUES('robot_observator_mote','B0:B4:48:ED:99:01',datetime(),datetime());
/* Insert Queries */
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('ble_advertisments','wsn/ble/devices/advertisments',datetime(), datetime(),1,1);
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('monitoring','atlas/monitoring/gateways/{gateway-identity}',datetime(), datetime(),1,1);
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('commands_response','atlas/commands/gateways/{gateway-identity}/response',datetime(), datetime(),2,1);
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('device_authentication','atlas/devices/auth',datetime(), datetime(),2,1);
/** Only for now, until the Unity Application is 100% ready. - atlas/data/gateways/{gateway-identity}**/
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('device_data','atlas/notifications',datetime(), datetime(),1,1);
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('apps/notifications','atlas/notifications',datetime(), datetime(),1,1);
INSERT INTO routes (source,destination,createdAt,updatedAt,qos,status) VALUES('apps/alerts','atlas/alerts',datetime(), datetime(),1,1);

