package jp.co.kaiwaredaikon320.syushi;


public class Ngram {

    /**
     * N-gram変換したテキストを返す
     * @param aText N-gram変換するテキスト
     * @param aN N
     * @return N-gram変換されたテキスト
     */
    public static String getNgramText( final String aText, final int aN ) {

        final StringBuilder ngramText = new StringBuilder();
        final int length = aText.length();

        if ( length < 1 ) {

            return "";
        }

        if ( length > aN ) {

            int roop = length - aN;

            for ( int i = 0; i < roop; i++ ) {

                ngramText.append( aText.substring( i, i + aN ) );
                ngramText.append( " " );

                if ( i == ( roop - 1 ) ) {

                    i++;
                    ngramText.append( aText.substring( i, i + aN ) );

                    if ( i % aN >= aN ) {

                        i++;
                        ngramText.append( " " );
                        ngramText.append( aText.substring( i, i + aN ) );
                    }
                }
            }

        } else {

            ngramText.append( aText );
        }

        return ngramText.toString();
    }
}
