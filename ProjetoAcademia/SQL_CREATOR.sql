-- Script para o banco ACADEMIA

-- Caso queira criar o banco (roda só uma vez)
-- CREATE DATABASE ACADEMIA;

-- Tabela endereco
CREATE TABLE IF NOT EXISTS endereco (
  id SERIAL PRIMARY KEY,
  rua VARCHAR(100),
  numero VARCHAR(100),
  bairro VARCHAR(255),
  cep VARCHAR(100),
  complemento VARCHAR(255)
);

-- Tabela aluno
CREATE TABLE IF NOT EXISTS aluno (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(255),
  telefone VARCHAR(100),
  telefone_emergencia VARCHAR(100),
  tipo_treino VARCHAR(100),
  problemas VARCHAR(255),
  endereco_id INT,
  CONSTRAINT fk_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id) ON DELETE CASCADE
);

-- Tabela professor
CREATE TABLE IF NOT EXISTS professor (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(255),
  telefone VARCHAR(100),
  telefone_emergencia VARCHAR(100),
  endereco_id INT,
  CONSTRAINT fk_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grupo (
    id SERIAL PRIMARY KEY,
    professor_id INTEGER NOT NULL,
    FOREIGN KEY (professor_id) REFERENCES professor(id)
);

CREATE TABLE IF NOT EXISTS horario (
    id SERIAL PRIMARY KEY,
    dia_semana VARCHAR(10) NOT NULL, -- Exemplo: 'Segunda', 'Terca', ...
    hora_inicio TIME NOT NULL
);

CREATE TABLE grupo_horario (
    grupo_id INTEGER NOT NULL,
    horario_id INTEGER NOT NULL,
    PRIMARY KEY (grupo_id, horario_id),
    FOREIGN KEY (grupo_id) REFERENCES grupo(id),
    FOREIGN KEY (horario_id) REFERENCES horario(id)
);

CREATE TABLE grupo_aluno (
    grupo_id INTEGER NOT NULL,
    aluno_id INTEGER NOT NULL,
    PRIMARY KEY (grupo_id, aluno_id),
    FOREIGN KEY (grupo_id) REFERENCES grupo(id),
    FOREIGN KEY (aluno_id) REFERENCES aluno(id)
);

-- Função atualizada para deletar endereço se não tiver mais aluno nem professor
CREATE OR REPLACE FUNCTION deletar_endereco_sem_vinculo()
RETURNS TRIGGER AS $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM aluno WHERE endereco_id = OLD.endereco_id
  ) AND NOT EXISTS (
    SELECT 1 FROM professor WHERE endereco_id = OLD.endereco_id
  ) THEN
    DELETE FROM endereco WHERE id = OLD.endereco_id;
  END IF;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger atualizada para chamar a função após deletar um aluno
DROP TRIGGER IF EXISTS trigger_delete_endereco ON aluno;
CREATE TRIGGER trigger_delete_endereco
AFTER DELETE ON aluno
FOR EACH ROW
EXECUTE FUNCTION deletar_endereco_sem_vinculo();

-- Cria a mesma trigger na tabela professor
DROP TRIGGER IF EXISTS trigger_delete_endereco_prof ON professor;
CREATE TRIGGER trigger_delete_endereco_prof
AFTER DELETE ON professor
FOR EACH ROW
EXECUTE FUNCTION deletar_endereco_sem_vinculo();

-- Insere horários de Segunda a Sexta, das 05:00 às 22:00
INSERT INTO horario (dia_semana, hora_inicio)
SELECT dia, (hora::time)
FROM (
    SELECT unnest(ARRAY['Segunda', 'Terca', 'Quarta', 'Quinta', 'Sexta']) AS dia
) dias,
generate_series(
    timestamp '2025-01-01 05:00:00',
    timestamp '2025-01-01 22:00:00',
    interval '1 hour'
) AS hora;

-- Insere horários de Sábado, das 05:00 às 11:00
INSERT INTO horario (dia_semana, hora_inicio)
SELECT 'Sabado', (hora::time)
FROM generate_series(
    timestamp '2025-01-01 05:00:00',
    timestamp '2025-01-01 11:00:00',
    interval '1 hour'
) AS hora;
