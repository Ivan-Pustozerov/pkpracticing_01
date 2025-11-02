package functions.classes;

import functions.interfaces.MathFunction;
public class SimpleIterationMethod implements MathFunction{

    protected double epsilon;       //некая погрешность
    protected int maxIter;          //предельное число итераций
    protected double [][] MatA;     //матрица СЛАУ
    protected double [] VecB;       //вектор правых частей СЛАУ
    protected double[] Approx;      //приближение(задается изначально)

    public SimpleIterationMethod( double epsilon, int maxIter,double[][] MatA, double [] VecB,double[] Approx){
        this.epsilon=epsilon;
        this.maxIter=maxIter;
        this.MatA=MatA;
        this.VecB=VecB;
        this.Approx=Approx.clone();

    }

    protected boolean Isconverge(){
        if (!IsDominant()){
            ReworkMat();
            return IsDominant();
        }else {
            return true;
        }
    }

    protected boolean IsDominant(){
        for(int i=0;i<MatA.length;i++){
            double di = Math.abs(MatA[i][i]);
            double sum = 0;
            for (int j = 0; j < MatA.length; j++) {
                if (j != i) sum += Math.abs(MatA[i][j]);
            }
            if (di <= sum) return false;
        }
        return true;
    }

    protected void ReworkMat(){
        int n = MatA.length;
        double[][] newA = new double[n][n];
        double[] newB = new double[n];
        boolean[] used = new boolean[n];

        for (int i = 0; i < n; i++) {
            int maxRow = -1;
            double maxDiag = -1;
            for (int k = 0; k < n; k++) {
                if (!used[k] && Math.abs(MatA[k][i]) > maxDiag) {
                    maxDiag = Math.abs(MatA[k][i]);
                    maxRow = k;
                }
            }
            if (maxRow == -1) break;
            used[maxRow] = true;
            newA[i] = MatA[maxRow].clone();
            newB[i] = VecB[maxRow];
        }
        MatA = newA;
        VecB = newB;
    }

    protected double[] Iterate(double[] X){
        double[] xNext = new double[VecB.length];
        for (int i=0; i < VecB.length; i++){
            double SUM = VecB[i];
            for (int j=0; j < VecB.length; j++){
                if (j != i){
                    SUM -= MatA[i][j] * X[j];
                }
            }
            xNext[i] = SUM/MatA[i][i];
        }
        return xNext;
    }

    protected boolean converged(double[] oldx, double[] newx) {
        double maxD = 0;
        for (int i = 0; i < newx.length; i++) {
            double diff = Math.abs(newx[i] - oldx[i]);
            if (diff > maxD) maxD = diff;
        }
        return maxD < epsilon;
    }

    @Override
    public <T extends Number> double apply(T x)
    {
        if (!(x instanceof Integer)) return Double.NaN;
        int index = x.intValue();
        if (index < 0 || index >= VecB.length) return Double.NaN;

        if (!Isconverge()) return Double.NaN;
        double[] xNext;
        for (int iterC=0; iterC < maxIter; iterC++) {
            xNext = Iterate(Approx);
            if (converged(Approx, xNext)){
                Approx = xNext;
                break;
            }
            Approx = xNext;
        }
        return Approx[index];
    }

}
