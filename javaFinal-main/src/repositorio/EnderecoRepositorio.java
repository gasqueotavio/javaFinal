package repositorio;

import model.*;
import java.util.ArrayList;
import java.util.List;

public final class EnderecoRepositorio extends Repositorio<Endereco> {

    public EnderecoRepositorio(String caminhoArquivo) {
        super(caminhoArquivo);
        carregar();
    }

    @Override
    public void carregar() {
        dados.clear();
        for (String linha : lerLinhas()) {
            Endereco e = desserializar(linha);
            if (e != null) dados.add(e);
        }
    }

    @Override
    protected Endereco desserializar(String linha) {
        String[] p = linha.split("\\|", -1);
        if (p.length < 7) return null;
        try {
            int id = Integer.parseInt(p[0]);
            TipoEndereco tipo = TipoEndereco.valueOf(p[6]);
            return new Endereco(id, p[1], p[2], p[3], p[4], p[5], tipo);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String serializar(Endereco e) {
        return e.serializar();
    }

    public int proximoId() {
        int max = 0;
        for (Endereco e : dados) {
            if (e.getId() > max) max = e.getId();
        }
        return max + 1;
    }

    public Endereco buscarPorId(int id) {
        for (Endereco e : dados) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public boolean incluir(Endereco e) {
        dados.add(e);
        salvar();
        return true;
    }

    public boolean alterar(Endereco atualizado) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getId() == atualizado.getId()) {
                dados.set(i, atualizado);
                salvar();
                return true;
            }
        }
        return false;
    }

    public boolean excluir(int id) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getId() == id) {
                dados.remove(i);
                salvar();
                return true;
            }
        }
        return false;
    }

    public List<Endereco> listarPorPessoa(String codigoPessoa) {
        List<Endereco> resultado = new ArrayList<>();
        for (Endereco e : dados) {
            if (e.getCodigoPessoa().equalsIgnoreCase(codigoPessoa)) {
                resultado.add(e);
            }
        }
        return resultado;
    }
}
