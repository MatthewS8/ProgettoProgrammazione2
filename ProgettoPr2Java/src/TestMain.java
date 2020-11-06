import java.util.Iterator;
import java.util.List;

public class TestMain {

    public static void main(String [] args){
        Board<DataImage> di = null , di2 = null, di3;
        final String u1 = "GiulioCesare", p1 = "SPQR3";
        final String u2 = "TheAnswer", p2 ="Its42";
        final String u3 = "Trentatre", p3 = "Trentini";
        final String cate1 = "Prima Categoria" , cate2 = "Seconda categoria", cate3 = "Terza categoria";

        try{
            di = new Board<>(u1,p1);
            di2 = new Board<>(u2,p2);
            di3 = new Board<>(u3,p3);
        }catch (UserAlreadyExistsException e){
            e.printStackTrace();
        }

        //test creazione board con utente gia registrato
        try{
            Board<DataImage> testError = new Board<>(u1,p1);
        }catch(UserAlreadyExistsException e){
            System.out.println("Test duplicato utente passato");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            di.createCategory(cate1, p1);
            di.createCategory(cate2, p1);
            di.createCategory(cate3,p1);
        }catch(InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled Exception");
            e.printStackTrace();
        }

        //Test duplicazione categoria
        try{
            di.createCategory(cate1,p1);
        }catch(InvalidOperationException e){
            System.out.println("Test duplicato categoria superato");
        }catch(Exception e){
            e.printStackTrace();
        }

        DataImage roma = new DataImage("Roma.jpeg");
        DataImage asterix = new DataImage("Asterix.jpeg");
        DataImage denario = new DataImage("Moneta.png");
        DataImage Cleopatra = new DataImage("Cleopatra.jpg");
        DataImage Bruto = new DataImage("MeAndBruto.jpeg");
        DataImage CesareSelfie = new DataImage("GiulioCesareSelfie.jpeg");
        try {
            di.put(p1, roma, cate1);
            di.put(p1, asterix, cate2);
            di.put(p1, denario, cate3);
            di.put(p1, Cleopatra, cate1);
            di.put(p1, Bruto, cate2);
            di.put(p1,CesareSelfie,cate1);
            di.put(p1,CesareSelfie,cate3);
            di.put(p1, new DataImage("Idefix"), cate1);
        }catch (InvalidOperationException | IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }
        //test categoria non esistente
        try {
            di.put(p1, Bruto, "CategorynonEsistente");
        }catch (InvalidOperationException e){
            System.out.println("Test categoria non esistente passato");
        }catch (Exception e){
            System.err.println("test fallito");
            e.printStackTrace();
        }


        try {
            di.addFriend(cate1, p1, u2);
            di.addFriend(cate2, p1, u2);
            di.addFriend(cate1, p1, u3);
            di.addFriend(cate2, p1, u3);
            di.addFriend(cate3, p1, u3);
        }catch (InvalidOperationException | IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }
        // duplicato friend in same category
        try {
            di.addFriend(cate1, p1, u2);
        }catch (InvalidOperationException | IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }
        try {
            di.removeFriend(cate2, p1, u2);
        }catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }

        try{
            di.removeCategory(cate2,p1);
        }catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e){
            System.err.println("Unhandled"); e.printStackTrace();
        }

        try {
            di.insertLike(u2, roma); //works
            di.insertLike(u2, asterix); //category removed
            di.insertLike(u2, Bruto); //category removed
            di.insertLike(u2, Cleopatra); //works
            di.insertLike(u3, denario); //works
            di.insertLike(u3, roma); //works
            di.insertLike(u2, CesareSelfie);
            di.insertLike(u3,CesareSelfie);
        } catch ( IllegalArgumentException | UserAlreadyExistsException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        DataImage testGet = null;
        try{
            testGet = di.get(p1,new DataImage("Roma.jpeg"));
            if(testGet == null) throw new InvalidOperationException("test failed");
        }catch (IllegalArgumentException  | InvalidOperationException e){
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        //test 2 like allo stesso post dallo stesso utente
        try {
            di.insertLike(u2, testGet); //Roma
        } catch ( UserAlreadyExistsException e) {
            System.out.println("test 2 like allo stesso post dallo stesso utente passato");
        }catch(Exception e) {
            System.err.println("Failed"); e.printStackTrace();
        }

        try{
            testGet = di.remove(p1,new DataImage("Idefix"));
        } catch (InvalidOperationException |IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        List<DataImage> ls = null;
        // visione categoria 1
        try {
            ls = di.getDataCategory(p1,cate1);
        } catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }

        System.out.println("Stampa categoria 1");
        try {
            for (DataImage d : ls) d.display();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Iterator<DataImage> iter = di.getIterator(p1);
            System.out.println("====  Stampa elementi in ordine di like ====");
            while(iter.hasNext()){
                iter.next().display();
            }
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }

        //test remove() iteratore
        try {
            Iterator<DataImage> iter = di.getIterator(p1);
            while(iter.hasNext()){
                iter.remove();
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("Test remove iterator passed");
        }catch(Exception e) {
            System.err.println("Failed"); e.printStackTrace();
        }

        Iterator<DataImage> u2Iterator = di.getFriendIterator(u2);
        System.out.println("Dati di "+ u1 + " condivisi con "+u2);
        while(u2Iterator.hasNext()){
            u2Iterator.next().display();
        }

        //TEST CHE NON PROVOCANO ERRORI MA CHE IGNORANO L-ESECUZIONE DEL COMANDO

        //duplicato friend nella stessa categoria
        try{
            di.addFriend(cate1,p1,u2);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }

        //dato duplicato nella stessa categoria
        try {
            di.put(p1,roma,cate1);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }
        //addlike di friend che non ha accesso alla categoria
        try{
            di.insertLike(u2,denario);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        }

        try {
            ls = di.getDataCategory(p1,cate3);
        } catch (InvalidOperationException | IllegalArgumentException e) {
            e.printStackTrace();
        }catch(Exception e) {
            System.err.println("Unhandled"); e.printStackTrace();
        }
        System.out.println("Stampa categoria 3");
        try {
            for (DataImage d : ls) d.display();
        } catch (Exception e) {
            e.printStackTrace();
        }


        TestMainB2.test2();
    }
}
