-- Script para o banco ACADEMIA

-- Caso queira criar o banco (roda só uma vez)
-- CREATE DATABASE ACADEMIA;

-- Tabela endereco
CREATE TABLE endereco (
  id SERIAL PRIMARY KEY,
  rua VARCHAR(100),
  numero VARCHAR(100),
  bairro VARCHAR(255),
  cep VARCHAR(100),
  complemento VARCHAR(255)
);

-- Tabela aluno
CREATE TABLE aluno (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(255),
  telefone VARCHAR(100),
  telefone_emergencia VARCHAR(100),
  tipo_treino VARCHAR(100),
  endereco_id INT,
  CONSTRAINT fk_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id) ON DELETE CASCADE
);

-- Função para deletar endereço se não tiver mais aluno vinculado
CREATE OR REPLACE FUNCTION deletar_endereco_sem_aluno()
RETURNS TRIGGER AS $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM aluno WHERE endereco_id = OLD.endereco_id
  ) THEN
    DELETE FROM endereco WHERE id = OLD.endereco_id;
  END IF;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger que chama a função após deletar um aluno
CREATE TRIGGER trigger_delete_endereco
AFTER DELETE ON aluno
FOR EACH ROW
EXECUTE FUNCTION deletar_endereco_sem_aluno();
