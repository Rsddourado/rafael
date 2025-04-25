import javax.swing.*; // Componentes gráficos (JFrame, JLabel, JTextField, etc.)
import java.awt.*; // Gerenciadores de layout como BorderLayout, FlowLayout
import java.awt.event.*; // Eventos como pressionar Enter
import java.io.*; // Para leitura e escrita de dados
import java.net.*; // Para comunicação via rede com Socket

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente - Chat"); // Cria a janela principal com título
        JTextArea chatArea = new JTextArea(); // Área onde as mensagens do chat vão aparecer
        JTextField inputField = new JTextField(); // Campo onde o usuário digita a mensagem
        
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Painel com layout para os campos de IP e porta

        JLabel ipLabel = new JLabel("IP:"); // Rótulo para o campo de IP
        JTextField ipField = new JTextField(10); // Campo de texto para digitar o IP (10 colunas)

        JLabel portLabel = new JLabel("Porta:"); // Rótulo para o campo de porta
        JTextField portField = new JTextField(5); // Campo de texto para digitar a porta (5 colunas)

        connectionPanel.add(ipLabel); // Adiciona o rótulo de IP ao painel
        connectionPanel.add(ipField); // Adiciona o campo de IP ao painel
        connectionPanel.add(portLabel); // Adiciona o rótulo de porta ao painel
        connectionPanel.add(portField); // Adiciona o campo de porta ao painel

        chatArea.setEditable(false); // Impede o usuário de digitar direto na área de chat

        frame.setLayout(new BorderLayout()); // Define o layout da janela como BorderLayout
        frame.add(connectionPanel, BorderLayout.NORTH); // Adiciona o painel de conexão na parte superior
        frame.setSize(400, 300); // Define o tamanho da janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o programa ao clicar no "X"
        frame.setVisible(true); // Torna a janela visível

        ipField.addActionListener(e -> conectar(frame, chatArea, inputField, ipField, portField)); // Conecta ao apertar Enter no campo IP
        portField.addActionListener(e -> conectar(frame, chatArea, inputField, ipField, portField)); // Conecta ao apertar Enter no campo porta
    }

    public static void conectar(JFrame frame, JTextArea chatArea, JTextField inputField, JTextField ipField, JTextField portField) {
        try {
            String ip = ipField.getText(); // Pega o IP digitado
            int porta = Integer.parseInt(portField.getText()); // Converte a porta digitada em número

            Socket socket = new Socket(ip, porta); // Cria a conexão com o servidor usando IP e porta
            chatArea.append("Conectado ao servidor!\n"); // Mostra que conectou com sucesso

            frame.remove(ipField.getParent().getParent()); // Remove o painel de conexão da janela
            frame.add(new JScrollPane(chatArea), BorderLayout.CENTER); // Adiciona a área de chat (com rolagem) no centro
            frame.add(inputField, BorderLayout.SOUTH); // Adiciona o campo de digitação na parte de baixo
            frame.revalidate(); // Atualiza a janela com os novos componentes

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Leitor das mensagens vindas do servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Escritor para enviar mensagens ao servidor

            inputField.addActionListener(a -> { // Quando o usuário pressiona Enter no campo de mensagem
                String msg = inputField.getText(); // Pega a mensagem digitada
                chatArea.append("Cliente: " + msg + "\n"); // Mostra a mensagem no chat local
                out.println(msg); // Envia a mensagem para o servidor
                inputField.setText(""); // Limpa o campo de digitação
            });

            new Thread(() -> { // Cria uma nova thread para escutar mensagens do servidor
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) { // Enquanto receber mensagem do servidor
                        chatArea.append("Servidor: " + msg + "\n"); // Exibe a mensagem recebida
                    }
                } catch (IOException e) {
                    chatArea.append("Erro: " + e.getMessage() + "\n"); // Mostra erro de leitura
                }
            }).start(); // Inicia a thread

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Erro ao conectar: " + ex.getMessage()); // Msg de erro 
        }
    }
}
tem que ir pra master 