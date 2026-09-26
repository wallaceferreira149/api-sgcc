-- Catálogo inicial de tipos de aeronave da FAB.
--
-- Executado apenas no perfil "dev" (ver spring.sql.init em application-dev.yaml).
-- O schema é recriado a cada boot (ddl-auto: create-drop), por isso a base nasce vazia
-- e os INSERTs abaixo não precisam ser idempotentes.
--
-- O código é gravado no formato canônico com hífen. A checagem de duplicidade em
-- AircraftTypeRepository (existsByCodeKey) ignora hífen, espaço e caixa, então cadastrar
-- "A29" para uma aeronave que já existe como "A-29" é rejeitado com HTTP 409.
INSERT INTO aircraft_types (code) VALUES ('A-1');
INSERT INTO aircraft_types (code) VALUES ('A-29');
INSERT INTO aircraft_types (code) VALUES ('F-5');
INSERT INTO aircraft_types (code) VALUES ('F-39');
INSERT INTO aircraft_types (code) VALUES ('R-35');
INSERT INTO aircraft_types (code) VALUES ('R-99');
INSERT INTO aircraft_types (code) VALUES ('E-99');
INSERT INTO aircraft_types (code) VALUES ('C-95');
INSERT INTO aircraft_types (code) VALUES ('C-97');
INSERT INTO aircraft_types (code) VALUES ('C-98');
INSERT INTO aircraft_types (code) VALUES ('C-105');
INSERT INTO aircraft_types (code) VALUES ('C-130');
INSERT INTO aircraft_types (code) VALUES ('KC-390');
INSERT INTO aircraft_types (code) VALUES ('KC-30');
INSERT INTO aircraft_types (code) VALUES ('VC-1');
INSERT INTO aircraft_types (code) VALUES ('VC-2');
INSERT INTO aircraft_types (code) VALUES ('VC-99');
INSERT INTO aircraft_types (code) VALUES ('H-36');
INSERT INTO aircraft_types (code) VALUES ('H-50');
INSERT INTO aircraft_types (code) VALUES ('H-60');
INSERT INTO aircraft_types (code) VALUES ('AH-2');
INSERT INTO aircraft_types (code) VALUES ('T-25');
INSERT INTO aircraft_types (code) VALUES ('T-27');
INSERT INTO aircraft_types (code) VALUES ('P-3');
INSERT INTO aircraft_types (code) VALUES ('P-95');
INSERT INTO aircraft_types (code) VALUES ('SC-105');
INSERT INTO aircraft_types (code) VALUES ('RQ-450');
INSERT INTO aircraft_types (code) VALUES ('RQ-900');
INSERT INTO aircraft_types (code) VALUES ('RQ-1150');
