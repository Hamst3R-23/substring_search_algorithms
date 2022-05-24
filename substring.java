package vlad;

import java.util.ArrayList;
import java.util.Arrays;

public class substring {
    private static final int Q = 13;
    private static final int ASCII_alphabet_capacity = 256;

    //simple algorithm
    public static ArrayList<Integer> simple_search(String source, String sample) throws Exception {
        if(source == null || sample == null)
            throw new Exception("source string or sample is null");

        ArrayList<Integer> res = new ArrayList<>();
        if(source.length() == 0 || sample.length() == 0 || sample.length() > source.length())
            return res;

        int so_len = source.length();
        int sa_len = sample.length();

        for(int i = 0; i <= so_len - sa_len; i++)
            if(sample.equals(source.substring(i, i + sa_len)))
                res.add(i);

        return res;
    }

    //Rabin-Karp algorithm
    public static ArrayList<Integer> rabin_karp(String source, String sample) throws Exception {
        if(source == null || sample == null)
            throw new Exception("source string or sample is null");

        ArrayList<Integer> res = new ArrayList<>();

        if(source.length() == 0 || sample.length() == 0 || sample.length() > source.length())
            return res;

        int so_len = source.length();
        int sa_len = sample.length();

        int D = (int) (Math.pow(ASCII_alphabet_capacity, sa_len - 1) % Q);
        int sample_hash = get_hash(sample);
        int source_hash = get_hash(source.substring(0, sa_len));

        for(int i = 0; i <= so_len - sa_len; i++) {
            if(sample_hash == source_hash)
                if(sample.equals(source.substring(i, i + sa_len)))
                    res.add(i);
            if(i < so_len - sa_len) {
                source_hash = (ASCII_alphabet_capacity * (source_hash - D * source.charAt(i)) + source.charAt(i + sa_len)) % Q;
                if (source_hash < 0)
                    source_hash += Q;
            }
        }
        return res;
    }

    //for Rabin-Karp algorithm: getting hash
    private static int get_hash(String sample) {
        int hash = 0;
        for(int i = 0; i < sample.length(); i++)
            hash = (ASCII_alphabet_capacity * hash + sample.charAt(i)) % Q;
        return hash;
    }


    //state machine algorithm
    public static ArrayList<Integer> state_machine(String source, String sample) throws Exception {
        if(source == null || sample == null)
            throw new Exception("source string or sample is null");

        ArrayList<Integer> res = new ArrayList<>();

        if(source.length() == 0 || sample.length() == 0 || sample.length() > source.length())
            return res;

        int so_len = source.length();
        int sa_len = sample.length();
        int[][] table = new int[sa_len + 1][ASCII_alphabet_capacity];

        for(int i = 0; i < sa_len + 1; i++)
            for(int j = 0; j < ASCII_alphabet_capacity; j++)
                table[i][j] = suffix(i, (char) j, sample);

        int cur_state = 0;
        for(int pos = 0; pos < so_len; pos++) {
            cur_state = table[cur_state][source.charAt(pos) % ASCII_alphabet_capacity];
            if(cur_state == sa_len) {
                pos -= (sa_len - 1);
                res.add(pos);
            }
        }

        return res;
    }

    //for state machine algorithm: getting suffixes
    private static int suffix(int state, char cur_char, String sample) {
        if(state < sample.length() && cur_char == sample.charAt(state))
            return ++state;

        for(int currentStatus = state - 1; currentStatus >= 0; currentStatus--)
            if (sample.charAt(currentStatus) == cur_char)
                if(sample.substring(0, currentStatus).equals(sample.substring(state - currentStatus, state)))
                    return currentStatus + 1;

        return 0;
    }


    //Knuth-Morris-Pratt algorithm
    public static ArrayList<Integer> knuth_morris_pratt(String source, String sample) throws Exception {
        if(source == null || sample == null)
            throw new Exception("source string or sample is null");

        ArrayList<Integer> res = new ArrayList<>();

        if(source.length() == 0 || sample.length() == 0 || sample.length() > source.length())
            return res;

        int so_len = source.length();
        int sa_len = sample.length();

        int[] prefix_table = prefix_function(sample);
        int tmp = 0;
        for(int i = 0; i < so_len; i++) {
            while(tmp > 0 && sample.charAt(tmp) != source.charAt(i))
                tmp = prefix_table[tmp - 1];
            if(sample.charAt(tmp) == source.charAt(i))
                tmp = tmp + 1;
            if(tmp == sa_len) {
                res.add(i - sa_len + 1);
                tmp = prefix_table[tmp - 1];
            }
        }
        return res;
    }

    //for Knuth-Morris-Pratt algorithm: getting "prefix table"
    private static int[] prefix_function(String sample) {
        int[] prefix_table = new int[sample.length()];
        prefix_table[0] = 0;
        int tmp;
        for(int i = 1; i < sample.length(); i++) {
            tmp = prefix_table[i - 1];
            while(tmp > 0 && sample.charAt(tmp) != sample.charAt(i))
                tmp = prefix_table[tmp - 1];
            if(sample.charAt(tmp) == sample.charAt(i))
                tmp = tmp + 1;
            prefix_table[i] = tmp;
        }
        return prefix_table;
    }

    //Boyer-Moore algorithm
    public static ArrayList<Integer> boyer_moore(String source, String sample) throws Exception {
        if(source == null || sample == null)
            throw new Exception("source string or sample is null");

        ArrayList<Integer> res = new ArrayList<>();

        if(source.length() == 0 || sample.length() == 0 || sample.length() > source.length())
            return res;

        int so_len = source.length();
        int sa_len = sample.length();
        int delta_stop;
        int delta_suf;
        int[] suf = get_suf_table(sample);
        int[] stop = get_stop_table(sample);

        for(int i = 0; i <= so_len - sa_len;) {
            int j = sa_len - 1;

            while(j >= 0 && sample.charAt(j) == source.charAt(i + j))
                j--;

            if(j == -1) {
                res.add(i);
                delta_stop = 1;
            }
            else
                delta_stop = j - stop[source.charAt(i + j) % ASCII_alphabet_capacity];
            delta_suf = suf[j + 1];
            i += Math.max(delta_suf, delta_stop);
        }
        return res;
    }

    //for Boyer-Moore algorithm: getting "suffix table"
    private static int[] get_suf_table(String sample) {
        int sa_len = sample.length();
        int[] suf_table = new int[sa_len + 1];
        int[] p_1 = prefix_function(sample);

        StringBuilder stroka = new StringBuilder(sample);
        stroka.reverse();
        String inverted = stroka.toString();
        int[] p_2 = prefix_function(inverted);

        for(int i = 0; i < sa_len + 1; i++)
            suf_table[i] = sa_len - p_1[sa_len - 1];

        for(int i = 0; i < sa_len; i++) {
            int index = sa_len - p_2[i];
            int shift = i - p_2[i] + 1;
            if(suf_table[index] > shift)
                suf_table[index] = shift;
        }

        return suf_table;
    }

    //for Boyer-Moore algorithm: getting "stop table"
    private static int[] get_stop_table(String sample) {
        int[] stop_table = new int[ASCII_alphabet_capacity];
        Arrays.fill(stop_table, -1);
        for(int i = 0; i < sample.length() - 1; i++)
            if(stop_table[sample.charAt(i) % ASCII_alphabet_capacity] < i)
                stop_table[sample.charAt(i) % ASCII_alphabet_capacity] = i;
        return stop_table;
    }
}