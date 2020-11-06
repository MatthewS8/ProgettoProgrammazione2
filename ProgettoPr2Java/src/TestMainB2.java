import java.util.Iterator;
import java.util.List;

public class TestMainB2 {
    public static void test2() {

        Board<VideoData> vd = null , vd2 = null, vd3;
        final String v1 = "GiulioCesare_1", p1 = "SPQR3";
        final String v2 = "TheAnswerIs", p2 ="Its42";
        final String v3 = "Trentatre33", p3 = "Trentini";
        final String cate1 = "Prima_Categoria" , cate2 = "Seconda_categoria", cate3 = "Terza_categoria";

        try{
             vd = new Board<>(v1,p1);
            vd2 = new Board<>(v2,p2);
            vd3 = new Board<>(v3,p3);
        }catch (UserAlreadyExistsException e){
            e.printStackTrace();
        }

        //test creazione board con utente gia registrato
        try{
            Board<VideoData> testError = new Board<>(v1,p1);
        }catch(UserAlreadyExistsException e){
            System.out.println("Test duplicato utente passato");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            vd.createCategory(cate1, p1);
            vd.createCategory(cate2, p1);
            vd.createCategory(cate3,p1);
        }catch(InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled Exception");
            e.printStackTrace();
        }

        //Test duplicazione categoria
        try{
            vd.createCategory(cate1,p1);
        }catch(InvalidOperationException e){
            System.out.println("Test duplicato categoria superato");
        }catch(Exception e){
            e.printStackTrace();
        }

        VideoData roma = new VideoData("Roma.mp4",120);
        VideoData asterix = new VideoData("Asterix.mp4",80);
        VideoData denario = new VideoData("Moneta.gif",20);
        VideoData Cleopatra = new VideoData("Cleopatra.mp4",48);
        VideoData Bruto = new VideoData("MeAndBruto.mp4",6);
        VideoData CesareSelfie = new VideoData("GiulioCesareWalkOnLungoTevere.mp4",17);
        try {
            vd.put(p1, roma, cate1);
            vd.put(p1, asterix, cate2);
            vd.put(p1, denario, cate3);
            vd.put(p1, Cleopatra, cate1);
            vd.put(p1, Bruto, cate2);
            vd.put(p1,CesareSelfie,cate1);
            vd.put(p1,CesareSelfie,cate3);
            vd.put(p1, new VideoData("Idefix.mp4",4), cate1);
        }catch (InvalidOperationException | IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }
        //test categoria non esistente
        try {
            vd.put(p1, Bruto, "CategorynonEsistente");
        }catch (InvalidOperationException e){
            System.out.println("Test categoria non esistente passato");
        }catch (Exception e){
            System.err.println("test fallito");
            e.printStackTrace();
        }


        try {
            vd.addFriend(cate1, p1, v2);
            vd.addFriend(cate2, p1, v2);
            vd.addFriend(cate1, p1, v3);
            vd.addFriend(cate2, p1, v3);
            vd.addFriend(cate3, p1, v3);
        }catch (InvalidOperationException | IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }
        // duplicato friend in same category
        try {
            vd.addFriend(cate1, p1, v2);
        }catch (InvalidOperationException | IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }
        try {
            vd.removeFriend(cate2, p1, v2);
        }catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }

        try{
            vd.removeCategory(cate2,p1);
        }catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }

        try {
            vd.insertLike(v2, roma); //works
            vd.insertLike(v2, asterix); //category removed
            vd.insertLike(v2, Bruto); //category removed
            vd.insertLike(v2, Cleopatra); //works
            vd.insertLike(v3, denario); //works
            vd.insertLike(v3, roma); //works
            vd.insertLike(v2, CesareSelfie);
            vd.insertLike(v3,CesareSelfie);
        } catch ( IllegalArgumentException | UserAlreadyExistsException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        VideoData testGet = null;
        try{
            testGet = vd.get(p1,new VideoData("Roma.mp4",120));
            if(testGet == null) throw new InvalidOperationException("test failed");
        }catch (IllegalArgumentException  | InvalidOperationException e){
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        //test 2 like allo stesso post dallo stesso utente
        try {
            vd.insertLike(v2, testGet); //Roma
        } catch ( UserAlreadyExistsException e) {
            System.out.println("test 2 like allo stesso post dallo stesso utente passato");
        }catch(Exception e) {
            System.err.println("Failed"); e.printStackTrace();
        }

        try{
            testGet = vd.remove(p1,new VideoData("Idefix.mp4",4));
        } catch (InvalidOperationException |IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        List<VideoData> lsv = null;
        // visione categoria 1
        try {
            lsv = vd.getDataCategory(p1,cate1);
        } catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        System.out.println("Stampa categoria 1");
        try {
            for (VideoData d : lsv) d.display();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Iterator<VideoData> iter = vd.getIterator(p1);
            System.out.println("====  Stampa elementi in ordine di like ====");
            while(iter.hasNext()){
                iter.next().display();
            }
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }

        //test remove() iteratore
        try {
            Iterator<VideoData> iter = vd.getIterator(p1);
            while(iter.hasNext()){
                iter.remove();
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("Test remove iterator passed");
        }catch(Exception e) {
            System.err.println("Failed"); e.printStackTrace();
        }

        Iterator<VideoData> v2Iterator = vd.getFriendIterator(v2);
        System.out.println("Dati vd "+ v1 + " convdvisi con "+v2);
        while(v2Iterator.hasNext()){
            v2Iterator.next().display();
        }

        //TEST CHE NON PROVOCANO ERRORI MA CHE IGNORANO L-ESECUZIONE DEL COMANDO

        //duplicato friend nella stessa categoria
        try{
            vd.addFriend(cate1,p1,v2);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }

        //dato duplicato nella stessa categoria
        try {
            vd.put(p1,roma,cate1);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }
        //addlike di friend che non ha accesso alla categoria
        try{
            vd.insertLike(v2,denario);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        }

        try {
            lsv = vd.getDataCategory(p1,cate3);
        } catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }
        System.out.println("Stampa categoria 3");
        try {
            for (VideoData d : lsv) d.display();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
