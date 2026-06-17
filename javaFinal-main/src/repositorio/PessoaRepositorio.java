package repositorio;

import model.*;
import java.util.ArrayList;
import java.util.List;

public final class PessoaRepositorio extends Repositorio<Pessoa> {

    public PessoaRepositorio(String caminhoArquivo) {
        super(caminhoArquivo);
        carregar();
    }

    @Override
    public void carregar() {
        dados.clear();
        for (String linha : lerLinhas()) {
            Pessoa p = desserializar(linha);
            if (p != null) dados.add(p);
        }
    }

    @Override
    protected Pessoa desserializar(String linha) {
        String[] partes = linha.split("\\|", -1);
        if (partes.length < 3) return null;
        try {
            String codigo = partes[0];
            String nome = partes[1];
            TipoPessoa tipo = TipoPessoa.valueOf(partes[2]);
            switch (tipo) {
                case CLIENTE:    return new Cliente(codigo, nome);
                case FORNECEDOR: return new Fornecedor(codigo, nome);
                case AMBOS:      return new ClienteFornecedor(codigo, nome);
                default:         return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    protected String serializar(Pessoa p) {
        return p.serializar();
    }

    public Pessoa buscarPorCodigo(String codigo) {
        for (Pessoa p : dados) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) return p;
        }
        return null;
    }

    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    public boolean incluir(Pessoa p) {
        if (existeCodigo(p.getCodigo())) return false;
        dados.add(p);
        salvar();
        return true;
    }

    public boolean alterar(Pessoa pessoaAtualizada) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getCodigo().equalsIgnoreCase(pessoaAtualizada.getCodigo())) {
                dados.set(i, pessoaAtualizada);
                salvar();
                return true;
            }
        }
        return false;
    }

    public boolean excluir(String codigo) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getCodigo().equalsIgnoreCase(codigo)) {
                dados.remove(i);
                salvar();
                return true;
            }
        }
        return false;
    }

    public List<Pessoa> filtrarPorNome(String filtro) {
        List<Pessoa> resultado = new ArrayList<>();
        for (Pessoa p : dados) {
            if (p.getNome().toLowerCase().contains(filtro.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pessoa> listarFornecedores() {
        List<Pessoa> resultado = new ArrayList<>();
        for (Pessoa p : dados) {
            if (p.getTipo() == TipoPessoa.FORNECEDOR || p.getTipo() == TipoPessoa.AMBOS) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pessoa> listarClientes() {
        List<Pessoa> resultado = new ArrayList<>();
        for (Pessoa p : dados) {
            if (p.getTipo() == TipoPessoa.CLIENTE || p.getTipo() == TipoPessoa.AMBOS) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
