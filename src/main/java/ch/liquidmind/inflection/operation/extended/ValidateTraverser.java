package ch.liquidmind.inflection.operation.extended;

import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class ValidateTraverser extends LeftGraphTraverser
{
	public static final String DEFAULT_CONFIGURATION = ValidateTraverser.class.getName() + CONFIGURATION_SUFFIX;
	
	private List< ValidationError > validationErrors = new ArrayList< ValidationError >();
	
	public ValidateTraverser( HGroup hGroup )
	{
		this( hGroup, getConfiguration( DEFAULT_CONFIGURATION ) );
	}
	
	public ValidateTraverser( HGroup hGroup, VmapInstance configurationInstance )
	{
		super( hGroup, configurationInstance );
	}

	public List< ValidationError > getValidationErrors()
	{
		return validationErrors;
	}
}