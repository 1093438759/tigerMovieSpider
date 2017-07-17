package Arithmetic;

/**
 * Created by ty on 2017/7/7.
 */
public class Sort {

    //插入排序
    public static void inserSort() {
        int a[] = {49, 38, 65, 97, 76, 13, 27, 110, 250, 220, 189, 220, 49, 78, 34, 12, 64, 5, 62, 4, 99, 98, 54, 56, 17, 18, 23,
                34, 15, 35, 25, 53, 51};
        int temp = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = a.length - 1; j > i; j--) {
                if (a[i] > a[j]) {    //45  39
                    temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        for (int n = 0; n < a.length; n++)
            System.out.println(a[n]);
    }

    public static void hillShort() {
        int a[] = {1, 54, 6, 3, 78, 34, 12, 45, 56, 100};
        double d1 = a.length;
        int temp = 0;
        while (true) {
            d1 = Math.ceil(d1 / 2);
            int d = (int) d1;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < a.length; i += d) {
                    int j = i - d;
                    temp = a[i];
                    for (; j >= 0 && temp < a[j]; j -= d) {
                        a[j + d] = a[j];
                    }
                    a[j + d] = temp;
                }
            }
            if (d == 1) {
                break;
            }
        }
        for (int j:a)
        System.out.println(j);
    }

    public static void main(String[] args) {
       // Sort.inserSort();
        Sort.hillShort();
    }
}
