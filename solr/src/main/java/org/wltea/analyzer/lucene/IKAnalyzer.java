
package org.wltea.analyzer.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * 使用高版本lucene重写 IKAnalyzer（createComponents方法） 和 IKTokenizer（IKTokenizer方法）
 */
public final class IKAnalyzer extends Analyzer {

    private boolean useSmart;

    public boolean useSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public IKAnalyzer() {
        this(false);
    }

    public IKAnalyzer(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

	/*
	//Lucene4.7.2
	@Override
	protected TokenStreamComponents createComponents(String fieldName, final Reader in) {
		Tokenizer _IKTokenizer = new IKTokenizer(in, this.useSmart());
		return new TokenStreamComponents(_IKTokenizer);
	}
	*/

    /**
     * 支持 Lucene5.5.5
     *
     * @author kavin
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer _IKTokenizer = new IKTokenizer(this.useSmart());
        return new TokenStreamComponents(_IKTokenizer);
    }

}