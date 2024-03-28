DROP TABLE IF EXISTS myTable;

CREATE TABLE myTable (
  id mediumint(8) unsigned NOT NULL auto_increment,
  name varchar(255) default NULL,
  phone varchar(100) default NULL,
  email varchar(255) default NULL,
  company varchar(255),
  country varchar(100) default NULL,
  password TEXT default NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1;

INSERT INTO myTable (name, phone, email, company, country, password) VALUES
  ('Quon Berg', '(362) 231-3015', 'sollicitudin.a@outlook.ca', 'Ornare In Incorporated', 'Belgium', 'porta'),
  ('Hu Santos', '(643) 647-7329', 'ac.feugiat.non@yahoo.couk', 'Lacinia Vitae Limited', 'Canada', 'id'),
  ('Cailin Owens', '(532) 825-6066', 'ipsum.suspendisse@icloud.ca', 'Elit A Associates', 'Austria', 'ante'),
  ('Nicholas Vinson', '(252) 674-8581', 'tempor.est@aol.ca', 'Mauris Blandit Incorporated', 'South Korea', 'semper'),
  ('Denise Clay', '1-647-564-6530', 'justo.faucibus@aol.com', 'Nibh Limited', 'South Africa', 'mauris');
