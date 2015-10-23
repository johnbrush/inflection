package ch.liquidmind.inflection.model.linked;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

public class ViewLinked extends AliasableElementLinked implements View
{
	private Class< ? > viewedClass;
	private List< Class< ? > > usedClasses = new ArrayList< Class< ? > >();
	private TaxonomyLinked parentTaxonomyLinked;
	private List< MemberLinked > membersLinked = new ArrayList< MemberLinked >();

	public ViewLinked( String name )
	{
		super( name );
	}

	public Class< ? > getViewedClass()
	{
		return viewedClass;
	}

	public void setViewedClass( Class< ? > viewedClass )
	{
		this.viewedClass = viewedClass;
	}

	public List< Class< ? > > getUsedClasses()
	{
		return usedClasses;
	}

	public TaxonomyLinked getParentTaxonomyLinked()
	{
		return parentTaxonomyLinked;
	}

	public void setParentTaxonomyLinked( TaxonomyLinked parentTaxonomyLinked )
	{
		this.parentTaxonomyLinked = parentTaxonomyLinked;
	}

	public List< MemberLinked > getMembersLinked()
	{
		return membersLinked;
	}

	@Override
	public Taxonomy getParentTaxonomy()
	{
		return parentTaxonomyLinked;
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	public List< Member > getMembers()
	{
		return (List< Member >)(Object)getMembers( this );
	}
	
	@SuppressWarnings( "unchecked" )
	private static List< MemberLinked > getMembers( ViewLinked viewLinked )
	{
		List< MemberLinked > members = new ArrayList< MemberLinked >();
		
		if ( viewLinked != null )
		{
			List< MemberLinked > superViewMembers = getMembers( (ViewLinked)viewLinked.getSuperview() );
			List< MemberLinked > declaredMembers = (List< MemberLinked >)(Object)viewLinked.getDeclaredMembers();
			members.addAll( superViewMembers );
			
			for ( MemberLinked declaredMember : declaredMembers )
			{
				if ( containsMemberLinked( members, declaredMember.getName(), declaredMember.getSelectionType() ) )
					members.set( indexOfMemberLinked( members, declaredMember.getName(), declaredMember.getSelectionType() ), declaredMember );
				else
					members.add( declaredMember );
			}
		}
		
		return members;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public List< Member > getDeclaredMembers()
	{
		List< MemberLinked > declaredMembers = new ArrayList< MemberLinked >();
		
		// Pass 1: add includes
		for ( MemberLinked memberLinked : membersLinked )
			if ( memberLinked.getSelectionType().equals( SelectionType.INCLUDE ) )
				declaredMembers.add( memberLinked );
		
		// Pass 2: remove excludes
		for ( MemberLinked memberLinked : membersLinked )
			if ( memberLinked.getSelectionType().equals( SelectionType.EXCLUDE ) && containsMemberLinked( declaredMembers, memberLinked.getName(), SelectionType.INCLUDE ) )
				declaredMembers.remove( indexOfMemberLinked( declaredMembers, memberLinked.getName(), SelectionType.INCLUDE ) );		
		
		return (List< Member >)(Object)declaredMembers;
	}
	
//	// Pass 1: add includes
//	for ( ViewLinked unresolvedView : unresolvedViews )
//	{
//		if ( unresolvedView.getSelectionType().equals( SelectionType.INCLUDE ) )
//		{
//			if ( containsViewLinked( resolvedViews, unresolvedView.getName(), SelectionType.INCLUDE ) )
//				resolvedViews.set( indexOfViewLinked( resolvedViews, unresolvedView.getName(), unresolvedView.getSelectionType() ), unresolvedView );
//			else
//				resolvedViews.add( unresolvedView );
//		}
//	}
//	
//	// Pass 2: remove excludes
//	for ( ViewLinked unresolvedView : unresolvedViews )
//	{
//		if ( unresolvedView.getSelectionType().equals( SelectionType.EXCLUDE ) && containsViewLinked( resolvedViews, unresolvedView.getName(), SelectionType.INCLUDE ) )
//			resolvedViews.remove( indexOfViewLinked( resolvedViews, unresolvedView.getName(), SelectionType.INCLUDE ) );
//	}
	

	private static boolean containsMemberLinked( List< MemberLinked > membersLinked, String name, SelectionType selectionType )
	{
		return indexOfMemberLinked( membersLinked, name, selectionType ) == -1 ? false : true;
	}
	
	@SuppressWarnings( "unchecked" )
	private static int indexOfMemberLinked( List< MemberLinked > membersLinked, String name, SelectionType selectionType )
	{
		return SelectingElementLinked.indexOfSelectingElementLinked( (List< SelectingElementLinked >)(Object)membersLinked, name, selectionType );
	}
	
	@Override
	public List< Member > getUnresolvedMembers()
	{
		return ImmutableList.copyOf( membersLinked );
	}

	@Override
	public View getSuperview()
	{
		Class< ? > superclass = getViewedClass().getSuperclass();
		View superview = getParentTaxonomyLinked().resolveView( superclass );
		
		return superview;
	}
}
