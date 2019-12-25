package it.polito.dp2.RNS.sol1.readers;

import it.polito.dp2.RNS.IdentifiedEntityReader;
import it.polito.dp2.RNS.RnsReaderException;

public abstract class IdentifiedEntityReaderImpl 
	implements IdentifiedEntityReader 
{
	private String id;

	public IdentifiedEntityReaderImpl(String id) 
			throws RnsReaderException 
	{
		if(id == null)
			throw new RnsReaderException();
		
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
}
