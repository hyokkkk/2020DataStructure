
public class Pair<T1, T2>
{
	private T1 nth_row;
	private T2 jth_substring;

	Pair (T1 row, T2 substring) {
		this.nth_row = row;
		this.jth_substring = substring;
	}

	public void setNull ( ) {
		this.nth_row = null;
		this.jth_substring = null;
	}

	public T1 getN ( ) { return nth_row; }

	public T2 getJ ( ) { return jth_substring; }

	@ Override
	public String toString ( ) {
		return "(" + nth_row + ", " + jth_substring + ")";
	}
}

