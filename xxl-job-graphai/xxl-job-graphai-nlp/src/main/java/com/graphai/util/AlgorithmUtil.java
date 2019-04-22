package com.graphai.util;

public class AlgorithmUtil {



        public static void main(String[] args) {
            int floors = 6;
            int eggs = 2;

            System.out.println(computeMinDropsInWorstCase(eggs, floors));
        }

        // A utility function to get maximum of two integers
        static int max(int a, int b) { return (a > b)? a: b; }

        private static int computeMinDropsInWorstCase(int eggs, int floors) {
            int table[][]=new int[eggs+1][floors+1];


            // boundary condition:
            // if no floors or 1 floors, only need 0 trails or 1 trails

            for (int i = 0; i <= eggs; i++) {
                table[i][1] = 1;
                table[i][0] = 0;
            }


            // if only one egg,   f(1,k) = k

            for (int j = 0; j <= floors; j++) {
                table[1][j] = j;
            }

            // for the rest of cases
            // f( eggs, floors) = 1+ Max(f( eggs-1 , floors-1), f( eggs, floors-x))
            // x is the floor number we choose to drop for current attempt
            // range of i = 1,2,.....,floors,
            for(int i = 2; i <= eggs; i++)
            {
                for (int j = 2; j <= floors; j++) {

                    table[i][j] = Integer.MAX_VALUE;  // so important

                    for (int floorTriedFirst = 1; floorTriedFirst <= j; floorTriedFirst++) {
                        int res = 1+max(table[i-1][floorTriedFirst-1],
                                table[eggs][j-floorTriedFirst]);
                        if(res < table[i][j])
                        {
                            table[i][j] = res;
                        }
                    }

                }
            }

            return table[eggs][floors];
        }

}
