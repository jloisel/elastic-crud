package com.jeromeloisel.db.scroll.api;

import org.elasticsearch.action.search.SearchRequestBuilder;

public interface DatabaseScrollingFactory {

  DatabaseScrolling newScroll(String index);

  DatabaseScrolling newScroll(SearchRequestBuilder search);

  DatabaseScroll bulkDelete();

  DatabaseScroll bulkIndex();

  DatabaseScroll combine(DatabaseScroll... first);
}
