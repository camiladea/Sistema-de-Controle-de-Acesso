package service;

import java.util.Optional;
import com.nitgen.SDK.BSP.NBioBSPJNI;

public class LeitorBiometrico {

    private final NBioBSPJNI bsp;

    public LeitorBiometrico() {
        this.bsp = new NBioBSPJNI();
    }

    public void conectar() {
        bsp.OpenDevice(); // abre o dispositivo padrão
        if (bsp.IsErrorOccured()) {
            throw new IllegalStateException("Falha ao abrir o dispositivo: code=" + bsp.GetErrorCode());
        }
        System.out.println("LOG [LeitorBiometrico]: Dispositivo aberto.");
    }

    public void desconectar() {
        try {
            bsp.CloseDevice(); // fecha o que estiver aberto
            System.out.println("LOG [LeitorBiometrico]: Dispositivo fechado.");
        } catch (Throwable t) {
            System.err.println("Aviso: erro ao fechar dispositivo: " + t.getMessage());
        }
    }

    /**
     * Captura a digital do usuário e retorna o FIR em texto (TextFIR),
     * que é o que você salva no banco (coluna TEXT).
     */
    public Optional<String> lerDigital(String proposito) {
        System.out.println("LOG [LeitorBiometrico]: Capturando digital para: " + proposito);
        try {
            // 1) Captura para um handle
            NBioBSPJNI.FIR_HANDLE hCapturedFIR = bsp.new FIR_HANDLE();
            bsp.Capture(hCapturedFIR); // <- assinatura correta no SDK Nitgen

            if (bsp.IsErrorOccured()) {
                System.err.println("Erro na captura: code=" + bsp.GetErrorCode());
                return Optional.empty();
            }

            // 2) Converte o handle em TextFIR (string serializável)
            NBioBSPJNI.FIR_TEXTENCODE textFIR = bsp.new FIR_TEXTENCODE();
            bsp.GetTextFIRFromHandle(hCapturedFIR, textFIR);

            if (bsp.IsErrorOccured()) {
                System.err.println("Erro ao converter para TextFIR: code=" + bsp.GetErrorCode());
                return Optional.empty();
            }

            return Optional.ofNullable(textFIR.TextFIR);

        } catch (Exception e) {
            System.err.println("Exceção na captura: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Compara dois FIRs em texto (o capturado agora vs o salvo no BD).
     * Retorna true se corresponderem.
     */
    public boolean verificarDigital(String firCapturada, String firSalva) {
        try {
            // Monta INPUT_FIR 1
            NBioBSPJNI.INPUT_FIR input1 = bsp.new INPUT_FIR();
            NBioBSPJNI.FIR_TEXTENCODE t1 = bsp.new FIR_TEXTENCODE();
            t1.TextFIR = firCapturada;
            input1.SetTextFIR(t1);

            // Monta INPUT_FIR 2
            NBioBSPJNI.INPUT_FIR input2 = bsp.new INPUT_FIR();
            NBioBSPJNI.FIR_TEXTENCODE t2 = bsp.new FIR_TEXTENCODE();
            t2.TextFIR = firSalva;
            input2.SetTextFIR(t2);

            // Resultado (usa Boolean conforme o SDK mostra nos exemplos)
            Boolean bMatch = new Boolean(false);
            bsp.VerifyMatch(input1, input2, bMatch, null);

            if (bsp.IsErrorOccured()) {
                System.err.println("Erro no VerifyMatch: code=" + bsp.GetErrorCode());
                return false;
            }

            return bMatch.booleanValue();

        } catch (Exception e) {
            System.err.println("Erro ao verificar digital: " + e.getMessage());
            return false;
        }
    }
}
