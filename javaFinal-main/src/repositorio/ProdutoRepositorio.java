package repositorio;

import model.Produto;
import java.util.ArrayList;
import java.util.List;

public final class ProdutoRepositorio extends Repositorio<Produto> {

    public ProdutoRepositorio(String caminhoArquivo) {
        super(caminhoArquivo);
        carregar();
    }

    @Override
    public void carregar() {
        dados.clear();
        for (String linha : lerLinhas()) {
            Produto p = desserializar(linha);
            if (p != null) dados.add(p);
        }
    }

    @Override
    protected Produto desserializar(String linha) {
        String[] p = linha.split("\\|", -1);
        if (p.length < 5) return null;
        try {
            double custo = Double.parseDouble(p[2]);
            double preco = Double.parseDouble(p[3]);
            return new Produto(p[0], p[1], custo, preco, p[4]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected String serializar(Produto p) {
        return p.serializar();
    }

    public Produto buscarPorCodigo(String codigo) {
        for (Produto p : dados) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) return p;
        }
        return null;
    }

    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    public boolean incluir(Produto p) {
        if (existeCodigo(p.getCodigo())) return false;
        dados.add(p);
        salvar();
        return true;
    }

    public boolean alterar(Produto atualizado) {
        for (int i = 0; i < dados.size(); i++) {
            if (dados.get(i).getCodigo().equalsIgnoreCase(atualizado.getCodigo())) {
                dados.set(i, atualizado);
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

    public List<Produto> filtrarPorDescricao(String filtro) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : dados) {
            if (p.getDescricao().toLowerCase().contains(filtro.toLowerCase())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Produto> listarPorFornecedor(String codigoFornecedor) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : dados) {
            if (p.getCodigoFornecedor().equalsIgnoreCase(codigoFornecedor)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
