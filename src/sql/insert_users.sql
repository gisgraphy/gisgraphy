-- usage : psql -UYOURUSER -d DATABASE -f /path/to/file/insert_users.sql
-- this insert two user : an admin one (login : admin / password : admin) and a simple user (login : user / password : user)
--/!\ for security reasons, PLEASE, change the USER password by loging with the admin user on the admin interface : http://localhost:8080/admin/users.html,  after running this script. 

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- TOC entry 2410 (class 0 OID 1765263)
-- Dependencies: 2053
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO role (id, name, description) VALUES (-1, 'ROLE_ADMIN', 'Administrator role (can edit Users)');
INSERT INTO role (id, name, description) VALUES (-2, 'ROLE_USER', 'Default role for all Users');

INSERT INTO app_user (id, address, country, city, province, postal_code, version, account_enabled, username, password, email, password_hint, first_name, last_name, phone_number, website, account_expired, account_locked, credentials_expired) VALUES (-1, '', 'FR', 'Paris', 'Ile de France', '75000', 1, true, 'user', '12dea96fec20593566ab75692c9949596833adc9', 'GisgraphyUser@yourhost.com', 'Same as login.', 'Gisgraphy', 'User', '', 'http://www.gisgraphy.com', false, false, false);
INSERT INTO app_user (id, address, country, city, province, postal_code, version, account_enabled, username, password, email, password_hint, first_name, last_name, phone_number, website, account_expired, account_locked, credentials_expired) VALUES (-2, '', 'FR', 'Paris', 'Ile de France', '75000', 1, true, 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'davidmasclet@mymail.com', 'Same as login.', 'David', 'Masclet', '', 'http://www.gisgraphy.com', false, false, false);

INSERT INTO user_role (user_id, role_id) VALUES (-1, -2);
INSERT INTO user_role (user_id, role_id) VALUES (-2, -1);

-- Completed on 2008-07-06 08:51:30 CEST

--
-- PostgreSQL database dump complete
--

