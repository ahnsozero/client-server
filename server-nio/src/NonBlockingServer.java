import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonBlockingServer {

    public static void main(String[] args) throws IOException {
        int port = 9090;

        // 1. 서버 소켓 채널 열기
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false); // 논블로킹 모드

        // 2. 셀렉터 생성(단 한번만 생성)
        Selector selector = Selector.open();
        // 서버 채널을 selector에 등록하고, 클라이언트의 접속(ACCEPT) 이벤트를 감지하도록 설정
        // 클라이언트 접속시마다 채널 생성
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("서버가 포트" + port + "에서 시작되었습니다.");

        while (true) {
            // 1. selector가 등록된 채널들 중 이벤트가 발생한 채널을 감지
            selector.select();
            // 2. 감지된 이벤트 키(클라이언트 별의 키) 목록을 가져옴
            Iterator<SelectionKey> keyIterator = selector.selectedKeys()
                                                         .iterator(); // 셀렉터 키 반복자

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next(); // 하나씩 꺼냄
                keyIterator.remove(); // 다시 처리되지 않도록 제거

                // 3. 클라이언트의 이벤트가 접속 요청 이벤트(클라이언트가 연결 시도함)냐
                if (key.isAcceptable()) {
                    SocketChannel clientChannel = serverChannel.accept(); // 연결 수락
                    clientChannel.configureBlocking(false); // 논블로킹 모드 설정
                    clientChannel.register(selector, SelectionKey.OP_READ); // 데이터 수신 이벤트 감지 등록
                    System.out.println("클라이언트 연결됨: " + clientChannel.getRemoteAddress());

                    // 4. 클라이언트의 이벤트가 데이터 수신 이벤트냐
                } else if (key.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel(); // 이벤트 발생한 채널 추출
                    ByteBuffer buffer = ByteBuffer.allocate(1024); // 데이터 읽을 버퍼 생성
                    int bytesRead = clientChannel.read(buffer); // 채널에서 읽은 데이터를 버퍼에 쓴것

                    // byte가 -1이면 읽어올게 없다
                    if (bytesRead == -1) {
                        // 읽은 데이터가 없다 = 연결 종료
                        clientChannel.close();
                        System.out.println("클라이언트 연결 종료");

                        //  읽을 byte가 있다면
                    } else {
                        buffer.flip(); // 데이터를 쓰는(write) 모드에서 → 읽는(read) 모드로 바꿈
                        String received = new String(buffer.array(), 0, buffer.limit()); //버퍼에 있는 데이터를 문자열로 변환
                        System.out.println("수신: " + received);

                        // 서버에 응답
                        buffer.rewind(); // 버퍼의 position을 0으로 다시 설정
                        clientChannel.write(buffer); // 버퍼에 들어있는 데이터를 클라이언트에게 전송
                    }
                }
            }
        }
    }
}