/*
 *  Copyright 2017 Alexey Zhokhov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package graphql.datetime.boot.test

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.GraphQLSchema
import graphql.servlet.GraphQLServlet
import graphql.servlet.SimpleGraphQLServlet
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author <a href='mailto:alexey@zhokhov.com'>Alexey Zhokhov</a>
 */
class GraphQLServletSpec extends Specification {

    public static final String CONTENT_TYPE_JSON_UTF8 = 'application/json;charset=UTF-8'

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    AbstractApplicationContext context
    MockHttpServletRequest request
    MockHttpServletResponse response

    def setup() {
        context = ContextHelper.load()
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()
    }

    def cleanup() {
        if (context) {
            context.close()
            context = null
        }
    }

    def "test"() {
        given:
        GraphQLSchema graphQLSchema = context.getBean(GraphQLSchema.class)
        GraphQLServlet servlet = new SimpleGraphQLServlet(graphQLSchema)

        String query = """
{
    echo {
        date
        localDate
        localDateTime
        localTime
    }
}
"""

        when:
        servlet.executeQuery(query)

        then:
        response.getStatus() == 200
        response.getContentType() == CONTENT_TYPE_JSON_UTF8
        responseContent.data == [
                echo: [
                        date         : '2017-07-10T06:12:46.754Z',
                        localDate    : '2017-01-01',
                        localDateTime: '2017-01-01T00:00:00Z',
                        localTime    : '00:00:00'
                ]
        ]
    }

    private Map<String, Object> getResponseContent() {
        mapper.readValue(response.getContentAsByteArray(), Map)
    }

}
